Есть три микросервиса - kafka-producer, kafka-consumer, store-book-service.

Первый создаёт объект Book, ставит ему поле boolean checked = false и закидывает в общее хранилище сообщений для всех микросервисов (т.е. в очередь, из которой каждый микросервис может доставать сообщение). 

Отправляя объект Book, первый микросервис указывает ему определённый topic (тута - topic-one). Другие микросервисы внутри себя указывают тот топик, который они будут слушать. И если он совпадает с тем, что отправил первый микросервис - принимают его. 

Второй микросервис слушает topic-one, получает сообщение от первого, меняет поле checked на true и отправляет книгу в очередь кафки с топиком topic-two

Третий микросервис слушает topic-two и сохраняет полученную книгу в БД

# kafka-producer

### resources/docker-compose.yaml 
Kafka и Zookeper (Zookeper - типа БД, которую использует Kafka, без него работать не будет) - запускаются при помощи Docker и файла docker-compose.yaml. 

Контейнер Zookeper крутится на компе вот здесь: localhost:22181, но это не особо важно. У этого контейнера внутри крутится сам Zookeper, его порт - 2181. (дальше нам будет нужен только порт 2181)

Контейнер Kafka крутится на порту: localhost:29092, это тоже не особо важно. А вот сама Kafka внутри контейнера крутится также на порту 29092 (только теперь этот порт принадлежит не нашему компьютеру, а контейнеру - ведь он как виртуальная машина)

В контейнере Kafka мы подключаем зависимость на Zookeper. Указываем zookerper:2181, т.к. нам нужен не адрес контейнера на компьютере (т.е. localhost:22181), а адрес самого Zookeper внутри контейнера - zookeper:2181. (Kafka и Zookeper как бы крутятся в одной среде контейнеров, поэтому могут получать доступ друг к другу внутри контейнеров)

Остальные проперти неинтересные.

### model/Book
Это тот объект, который мы будем отправлять при помощи микросервиса kafka-producer и принимать при помощи микросервиса kafka-consumer. 

### kafka/KafkaConfig
Здесь мы создаём конфиг кафки - бин KafkaAdmin. В него засовывается вот такой адрес: localhost:29092. Это адрес контейнера, внутри которого крутится кафка на нашем компьютере.
И создаём NewTopic - "topic-one", его будет слушать kafka-consumer. 

### kafka/KafkaProducer
При помощи этого класса мы будем отправлять сообщения в Kafka. 

Инжектим зависимость на KafkaTemplate (класс, предоставляемый самой кафкой)

В методе sendMessage() мы берём книгу и конвертим при помощи ObjectMapper (на него нужна отдельная зависимость, так и называется - objectmapper) в JSON (это будет тип String, но вид как у JSON). Ставим книге топик, который мы создали в KafkaConfig. Отправляем книгу с указанным топиком в общую для всех микросервисов очередь. Выводим в консоль сообщение, что книга успешно отправлена. 

### controller/ProducerController
Инжектим KafkaProducer
По методу POST на адрес /send создаём книгу (мы пишем "Book" + book.hashcode() чтобы название было уникальным), ставим checked на false (его будет изменять второй микросервис, который примит объект книги) и id на null, т.к. третий микросервис будет сохранять книгу в БД, а там в БД будет выдан свой айдишник. 

И при помощи KafkaProducer мы помещаем объект книги в контекст кафки по указанному топику. 


### util/IntervalMessageSender
Замена ProducerController, где мы вручную вызывали KafkaProducer. Здесь идёт автоматический вызов KafkaProducer и метода sendMessage внутри него, и он делает запрос раз в 10 секунд, как на конвейере. Мне лень разбирать подробно внутренности. 


 
# kafka-consumer

### resources/application.properties
Указываем server.port, чтобы микросервисы смогли одновременно работать и не пытались занять один и тот же порт. 
Указываем адрес кафки localhost:29092. То же самое, что и в kafka-producer (там подробнее, что это за порт ваще)

### kafka/KafkaConfig
Всё то же самое, что и в kafka-producer, только мы создаём ещё один новый топик - topic-two. Этот топик будет слушать уже третий микросервис. 

### kafka/KafkaConsumer
Инжектим KafkaProducer (про него ниже)

Указываем над методом listener, что слушаем "topic-one" (это то, куда отправляет сообщения kafka-producer). А что такое group-id - я забыл уже. 

Инжектим ObjectMapper, при помощи него из message (это String в котором записан JSON книги) делаем объект Book. 

Устанавливаем книге поле checked в true

Отправляем её в очередь кафки при помощи KafkaProducer


### kafka/KafkaProducer
Инжектим kafkaTemplate, при помощи него будем отправлять сообщения в очередь кафки. 

Здест sendMessage принимает объект Book, так как sendMessage мы вызываем только из метода listen в KafkaConsumer. 

Переводим книгу в тип String, там будет книга в виде JSON. 

Ставим topic-two (его будет слушать третий микросервис) и отправляем книгу. 

Делаем запись в консоль, что книга отправлена. 



# store-book-service

### resources/application.properties
Что за server.port и spring.kafka.... - объяснял в kafka-consumer. 
Ещё указываем тут проперти для создания базы данных - туда будем записывать полученные из kafka-consumer книги. 

### KafkaConsumer
В целом, всё то же самое, что и в kafka-consumer, можно там подробнее прочитать. 
Просто получаем книгу, конвертим в Book.class (т.к. пришло в String в виде JSON), пишем в консоль, что получили, сохраняем, пишем в консоль, что сохранили


### bookService и bookRepository
bookService - просто обёртка над bookRepository, при помощи которого мы сохраняем значения в БД. 
