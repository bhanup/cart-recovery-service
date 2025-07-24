# cart-recovery-service

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./gradlew build
```

It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./gradlew build -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./gradlew build -Dquarkus.native.enabled=true
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/cart-recovery-service-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/gradle-tooling>.

## Related Guides

- Scheduler ([guide](https://quarkus.io/guides/scheduler)): Schedule jobs and tasks
- REST Jackson ([guide](https://quarkus.io/guides/rest#json-serialisation)): Jackson serialization support for Quarkus REST. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on it
- Messaging - Kafka Connector ([guide](https://quarkus.io/guides/kafka-getting-started)): Connect to Kafka with Reactive Messaging
- Kotlin ([guide](https://quarkus.io/guides/kotlin)): Write your services in Kotlin
- Reactive PostgreSQL client ([guide](https://quarkus.io/guides/reactive-sql-clients)): Connect to the PostgreSQL database using the reactive pattern

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)

Sample Payload for running the grpCurl
````
grpcurl -plaintext \
  -d '{
    "cart_id": "885e55cb-6c0d-4cfb-bf78-552bafbe5b39",
    "user": {
      "type": "USER_ID",
      "id": "user-456"
    },
    "action": "ITEM_ADDED",
    "event_time": "2025-07-24T03:01:00.123456789Z",
    "items": [
      {
        "product_id": "prod-789",
        "quantity": 2,
        "price_per_unit": {
          "currency_code": "INR",
          "amount": 149.50
        }
      }
    ],
    "source": "email"
  }' \
  localhost:9000 notifications.CheckoutService/CreateOrUpdateCart`
```

```
grpcurl -plaintext   -d '{
    "checkout_id": "885e55cb-6c0d-4cfb-bf78-552bafbe5b39",
    "user": {
      "type": "USER_ID",
      "id": "user-123"
    },
    "cart_id": "885e55cb-6c0d-4cfb-bf78-552bafbe5b38",
    "total_value": {
      "currency_code": "INR",
      "amount": 399.99
    },
    "checkout_time": "2023-10-27T10:30:00.123456789Z",
    "payment_status": "PAYMENT_PENDING"
  }'   localhost:9000 notifications.CheckoutService/CreateOrUpateCheckout

```
