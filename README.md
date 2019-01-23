Validator
=========

Simple json validator by scheme.

How to use
----------

java -jar validator.jar path_to_schema path_to_json

Result example
--------------

```
schema is invalid

#: required key [version] not found
#/event: 3 schema violations found
#/event/description/dateStart: [2018-08-012] is not a valid date. Expected [yyyy-MM-dd]
#/event/days/4: required key [id] not found
#/event/locations/0/lon: expected type: Number, found: String
```

Links
-----

Schema standard: [json-schema.org][1]
Validation library: [JSON Schema Validator][2]

[1]: https://json-schema.org/
[2]: https://github.com/everit-org/json-schema
