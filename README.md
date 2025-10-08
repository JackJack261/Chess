# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Web API Sequence Diagram

[Web API Sequence Diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwVygYKQH+iMyzKfMizfGpOx7Es0KPMB4lUEiMAIEJ4oYoJwkEkSYCkm+hi7jS+71PZwlTgZqn6H8Oxzj5d5LsKMBihKboynKZbvBWABkMAmQCmAqsGGpuka0AwE0IXqTA2QwAAkmgIDQCi4A3hFDrWbZPZ9tuXm+tU-qVdVxYoOAznilGMZxoUWnJsgqYwOmACMWbLDmqh5oZhbFtA9Q+NMl7QEgABeKC7DAdFNsOjUdZ2rozlunmCid9IwIecgoM+8QYuFvKRZUy4BmuAaXq+zrtQ8sL+gNaAZKoAGYDp8JnTULz6URhljN8FFUfWRmHehMPwBN2EwLh+GjAj5afJCsGXmjyGQpj9GMd4fj+F4KDoDEcSJEzLPOb4WCiYKoH1A00gRvxEbtBG3Q9HJqgKcMqOIegWPaZZ-oUdtO15AU9QADxy0h5RQ8r2OeX5Qnc05punq5ajuW1N23vyJv2NzU66+gb0LgKUX1LF67xdosryq7hTZeqv16jAlMyBAahoAA5MwVpovV72nR2NkujALX9td1ldVVNV9WAz2U0NKCxgpivjSUYACzNnRzWMC1LQWYxFiW62bXqav7eZjYMbdnuw7ZfvyMnHv3tFHAoNwx6XueFPy2gHkA0rwOllzlv-gggGG3znVgZp++CqJOF4VmR0MZ49MBCi67+Ng4oavxaIwAA4kqGi87npYNK-osS-YJUssF5IUrpUaG9QACQqsoC7Q1mgbWQd9bQz3udOyaJ345icmiK2JJbbeRTndZAORMFqHnvBRe7t9wT29uKX2l15ABzDpRReWVVQanFD1WqzACozGyNLBwxoQEKwHqg9OXZM69mzivb+9RuoF3AMXRepdy7xjGpUE+MBIENDrg3Ju+ZoJtzWgqTu8Ru4HQvmPahTUM4j2AFYj6Qo-IYI-q9TQOg7YNTuoGBAb8P4wAAFKqHjKImxEifF+JzIE4Jo0ZHgMNvUZ+ORwaQxQbImAekxiAJzAWBo4xskoHKtIAs01wjBECCCTY8RdQoDypBIyIJkigDVHU0myMQQFIAHJKiMhcGAnRD4SWxifAmDcCmqFyfkpURSSllIqcsKpNTWlI2+E0kALTEZmQ6Uqbpcxen9MsVfZi-gOAAHY3BOBQE4GIEZghwC4gANngBOQwpCYBFFxmJWGklWgdAAUA0xlMsxdKVIMxM8S15QJgXAkaiDhFoHKGMnZ9TkaYzSUPDOD10SkIxFilAuCbY5ypPbO6eKcVBzWCCuYVDHFfR9hud0-tEpKNASHDh+dergBgLw-h4pHBBwcanY2kjWpErTnnLhhcWWRmjGXEaYCcbVygTopw0165E0bvyZuhjVqlg2tCvaFi+6CsXBiiRdiTWDycTFOhkSUC7MMIlKlyp2F2qKZWM0FprQ5FtCSq1wrSEOvwd80sgZPVRhtCo+V6jFWTRVU4dVmT9HLVbrq+oeh1wokJNbXutNQlmu7FI4NMg-X1DJUqDEzqaWpy+o+O1-1Oyry-PUOALyUnbwNmvMRullgFJmfUUp5TMYxpGWfDVfbikDrmTTJsRyGaWGnvZTYrMkAJDAAuvsEBl0BIgOKO1MQ1lqg+dXbtP8mjMhkj0ApwCKFISzNgBAwAF1QDgBAeyUBKXTOkFcBVECtEGvgXC296BEWjAfU+ygr732frmEUn96K062QAFa7rQDilD4oCWkmLQPeoGG0MVopelR9z6oPQBg-apU2H83WvpXYph0rChsJyqVDl3DuWwD4fJQRAqaPCqzqYMV-MKpsalfCqNFcFWaO0bojVyaW5GL1aY8xubZ18YBhdTco9LU0JtRKQNFYnXIrmKYNlbrpBGGjqoOOCdvVYHU2ggTxbhPSCszZiNOQJPxik582ueFE3zS1QYla7dZQhhgFmtyqnL4OfEfUAzJmdNez0+udowYZhkdgIlcDpG33QFM66tLsxMseuDOaGAtYRF+rEbZJoJHIN5agM5-e9Qw1lcsGGJCXnYlDKrpNdMmY5NBZTYp+oJpPUVfDMhGdMXqthMLa1HDpaFTYC0Niwj8LVL1ZfY16jc3PrRWZKtx69atzFr-TuzDW8sCdq-Kel4P6R2+dGaMQ5TEGZeCfSutdn35SIGDLAYA2AH2EHge8r+IaBZCxFmLCWxhf0JIyRZLt82jDTyZCgaQOgMTSHR+iLDS2vGjjRzPTH2g9AGDcTRr6GQZgQBoJWXxa3gCsaMOT-QYhYvNSLWKv9uPSftp3ijyHSOfNKvxmOkYligA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
