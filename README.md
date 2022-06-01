# Congestion_Tax_Calculator
 

|  METHOD	      | PATH |
| ------------- | ------------- |
| POST  | api/v1/tax_calculation  |

## Request

      {
        "vehiclesList": [

          {
            "dates": [
              "2013-08-20 06:05:00",
              "2013-08-20 07:05:00",
              "2013-08-20 08:05:00",
              "2013-08-20 09:05:00",
              "2013-08-20 10:05:00"
            ],
            "vehicleId": 1234,
            "vehicleType": "BIKE",
            "vehicleName": "volvo"
          }
        ],
        "cityName": "Stockholm"
      }


## Response

     [
      {
        "vehicle": {
          "vehicleId": 1234,
          "vehicleType": "BIKE",
          "vehicleName": "volvo",
          "dates": 

          [

            "2013-08-20 06:05:00",
            "2013-08-20 07:05:00",
            "2013-08-20 08:05:00",
            "2013-08-20 09:05:00",
            "2013-08-20 10:05:00"

          ]
        },
        "message": "ONLY_VALID_DATES_DAYS_ARE_PRESENT",
        "tax": 39,
        "taxExemptedDates": []
      } 
    ]
  
