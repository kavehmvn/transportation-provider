# A Provider to simulate transporters for goods

It provides APIs to create transporter containers with maximum allowable gross load and load goods into them.

Each good has a price and a weight.
Each container can report at any time the most expensive good in the container.

As a rule, goods can be added to the container only from one side and should be unloaded from the same side in a LIFO manner. (For this purpose, it is implemented by using the java stack).

Data Export/import APIs are provided to import containers with goods as CSV files and export current containers as CSV files.

## Installation

Run the backend side in any Java IDE, and the APIs will be available on port 8081.
Swagger documentation of the APIs will also be available on: localhost:8081/swagger-ui.html#/
