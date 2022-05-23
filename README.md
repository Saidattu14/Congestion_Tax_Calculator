# Congestion_Tax_Calculator
 

|  METHOD	      | PATH |
| ------------- | ------------- |
| POST  | api/v1/tax_calculation  |

## Request

      {
     "vehiclesList": [
       {
         "dates": [
           "2013-02-28 06:19:09"
         ],
         "vehicle_id": 1234,
         "vehicle_type": "Bike",
         "vehicle_name": "volvo"
       },
       {
         "dates": [
           "2013-02-22 06:19:09",
           "2013-02-23 06:09:09"
         ],
         "vehicle_id": 1234,
         "vehicle_type": "Bike",
         "vehicle_name": "volvo"
       }
     ],
     "city_name": "Stockholm"
    }


## Response

     [
       {
         "vehicle": {
           "vehicle_id": 1234,
           "vehicle_type": "Bike",
           "vehicle_name": "volvo",
           "dates": [
             "2013-02-28 06:19:09"
           ]
         },
         "message": "Only_Tax_Exempted_Days_Are_Present",
         "tax": 0,
         "taxExemptedDates": [
           "2013-02-28 06:19:09"
         ],
         "errorDates": []
       },
       {
         "vehicle": {
           "vehicle_id": 1234,
           "vehicle_type": "Bike",
           "vehicle_name": "volvo",
           "dates": [
             "2013-02-22 06:19:09",
             "2013-02-23 06:09:09"
           ]
         },
         "message": "Valid_Dates_And_Tax_Exempted_Days_Are_Present",
         "tax": 8,
         "taxExemptedDates": [
           "2013-02-23 06:09:09"
         ],
         "errorDates": []
       }
     ]

  
