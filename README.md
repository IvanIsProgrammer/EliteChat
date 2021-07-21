# EliteChat
  Elite Chat - Сетевой чат, разработаный для ОС Android.
  Работает на моем сетевом модуле (network).

# Модуль network
  Данный модуль позволяет создать клиент-серверное соединение с помощью сокетов.
  Содержит 3 основных класса: Client, Server и Message.
  
  Server - класс, осуществляющий подключение, регистрацию/авторизацию, обмен сообщениями пользователей, хранит учетные записи пользователей.
  Интерфейс:
    = host(int port)  запускает сервер
    = stop()  останавливает сервер
    = isStarted()  возращает true если сервер запущен, иначе false
  Обработrf событий:
    = setOnClientConnected(Server.OnClientConnected onClientConnected)  событие подключения нового клиента
    = setOnClientDisconnected(OnClientDisconnected onClientDisconnected)  событие отключения клиента
    = setOnClientRegistration(OnClientRegistration onClientRegistration)  событие регистрации аккаунта клиента
    = setOnClientAuthorization(OnClientAuthorization onClientAuthorization)  событие авторизации аккаунта клиента
    = setOnServerStarted(OnServerStarted onServerStarted)  событие запуска работы сервера
    = setOnServerStopped(OnServerStopped onServerStopped)  событие остановки работы сервера
    
  Client - класс, осуществляющий подключение к серверу, регистрацию/авторизацию на сервере, отправку/прием сообщений сервера.
  Интерфейс:
    = connect(String address, int port) производит подключение к серверу || возращает true при удачном подключении иначе false
    = disconnect()  разрыв соединения с сервером
    = send(Message message)  отправляет сообщение на сервер
    = authorization(String login, String password)  отправляет запрос на авторизацию пользователя
    = registration(String login, String password)  отправляет запрос на регистрацию пользователя
  Обработка событий:
    = setOnNewMessageListener(OnNewMessageListener onNewMessageListener)  обработчик события нового подключения
    = setOnFailedConnectionListener(OnFailedConnection onFailedConnection)  событие неудачного подключения
    = setOnServerClose(OnServerClose onServerClose)  событие завершения работы сервера
    
  Message - класс, служащий пакетом, которым обмениваются клиенты и сервер. Содержит 3 открытых поля:
    = cmd - перечисление комманд
    = arguments - аргументы комманды
    = content - содержимое сообщения
