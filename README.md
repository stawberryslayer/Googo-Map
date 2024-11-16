# Googo-Map
UW-Madison CS407 project

## Development Conventions
- Create a branch from `main` to work on the development of feature.
- Once ready to be merged to main, make a pull request and request review from one teammate.
- Wait for review/approval before merging to main. This practice helps catch potential issues early and ensures code quality.

- Do NOT directly commit any code to the main branch.

- Commit Messages: Write clear, concise commit messages that explain whether you are **developing** certain feature or you are **fixing** some issues and what have you changed. 


### Sample Response
{
"geocoded_waypoints" :
[
{
"geocoder_status" : "OK",
"place_id" : "ChIJ4ealE82sB4gRNZ1TCDhQpM0",
"types" :
[
"street_address"
]
},
{
"geocoder_status" : "OK",
"place_id" : "ChIJm2r3xr-sB4gR7Xdad3lLXH8",
"types" :
[
"premise"
]
}
],
"routes" :
[
{
"bounds" :
{
"northeast" :
{
"lat" : 43.0776632,
"lng" : -89.40012
},
"southwest" :
{
"lat" : 43.0676517,
"lng" : -89.416489
}
},
"copyrights" : "Map data ©2024",
"legs" :
[
{
"distance" :
{
"text" : "1.5 mi",
"value" : 2364
},
"duration" :
{
"text" : "7 mins",
"value" : 392
},
"end_address" : "Bradley Residence Hall, 650 Elm Dr, Madison, WI 53706, USA",
"end_location" :
{
"lat" : 43.0776632,
"lng" : -89.41634990000001
},
"start_address" : "832 Regent St, Madison, WI 53715, USA",
"start_location" :
{
"lat" : 43.0676517,
"lng" : -89.40012
},
"steps" :
[
{
"distance" :
{
"text" : "203 ft",
"value" : 62
},
"duration" :
{
"text" : "1 min",
"value" : 10
},
"end_location" :
{
"lat" : 43.0676612,
"lng" : -89.4008844
},
"html_instructions" : "Head \u003cb\u003ewest\u003c/b\u003e on \u003cb\u003eRegent St\u003c/b\u003e toward \u003cb\u003eN Park St\u003c/b\u003e",
"polyline" :
{
"points" : "yszeGv}s`PAvC"
},
"start_location" :
{
"lat" : 43.0676517,
"lng" : -89.40012
},
"travel_mode" : "DRIVING"
},
{
"distance" :
{
"text" : "0.4 mi",
"value" : 624
},
"duration" :
{
"text" : "2 mins",
"value" : 115
},
"end_location" :
{
"lat" : 43.0732671,
"lng" : -89.4005555
},
"html_instructions" : "Turn \u003cb\u003eright\u003c/b\u003e at the 1st cross street onto \u003cb\u003eN Park St\u003c/b\u003e",
"maneuver" : "turn-right",
"polyline" :
{
"points" : "{szeGnbt`P_A?eB?o@AsBAa@Ay@CA?G?QAQAaAEE?sAEc@A}ACW?UAU?UCE?c@Eq@ISEQAKAE?M?[?E?{@AU@"
},
"start_location" :
{
"lat" : 43.0676612,
"lng" : -89.4008844
},
"travel_mode" : "DRIVING"
},
{
"distance" :
{
"text" : "0.5 mi",
"value" : 783
},
"duration" :
{
"text" : "2 mins",
"value" : 117
},
"end_location" :
{
"lat" : 43.0734116,
"lng" : -89.41018919999999
},
"html_instructions" : "Turn \u003cb\u003eleft\u003c/b\u003e onto \u003cb\u003eUniversity Ave\u003c/b\u003e",
"maneuver" : "turn-left",
"polyline" :
{
"points" : "}v{eGn`t`P?^AtACfCCtB?~BAb@?`AA`@?n@AV?XA`B?lB?p@?`BArCAxB?xD?v@?hAAV@fBAp@Aj@AZ"
},
"start_location" :
{
"lat" : 43.0732671,
"lng" : -89.4005555
},
"travel_mode" : "DRIVING"
},
{
"distance" :
{
"text" : "217 ft",
"value" : 66
},
"duration" :
{
"text" : "1 min",
"value" : 7
},
"end_location" :
{
"lat" : 43.0734576,
"lng" : -89.41100299999999
},
"html_instructions" : "Continue onto \u003cb\u003eCampus Dr\u003c/b\u003e",
"polyline" :
{
"points" : "yw{eGt|u`PCv@EhB"
},
"start_location" :
{
"lat" : 43.0734116,
"lng" : -89.41018919999999
},
"travel_mode" : "DRIVING"
},
{
"distance" :
{
"text" : "0.2 mi",
"value" : 276
},
"duration" :
{
"text" : "1 min",
"value" : 39
},
"end_location" :
{
"lat" : 43.0751523,
"lng" : -89.41273889999999
},
"html_instructions" : "Slight \u003cb\u003eright\u003c/b\u003e onto \u003cb\u003eBabcock Dr\u003c/b\u003e",
"maneuver" : "turn-slight-right",
"polyline" :
{
"points" : "cx{eGvav`PKb@Y`Aw@vDENAB?BABAB?@A@?@A@A@A@A@A@A@GFE@C@G@M@c@AK?mAAs@?"
},
"start_location" :
{
"lat" : 43.0734576,
"lng" : -89.41100299999999
},
"travel_mode" : "DRIVING"
},
{
"distance" :
{
"text" : "0.2 mi",
"value" : 279
},
"duration" :
{
"text" : "1 min",
"value" : 40
},
"end_location" :
{
"lat" : 43.0756416,
"lng" : -89.41588729999999
},
"html_instructions" : "Turn \u003cb\u003eleft\u003c/b\u003e onto \u003cb\u003eLinden Dr\u003c/b\u003e",
"maneuver" : "turn-left",
"polyline" :
{
"points" : "ub|eGrlv`PC`FArC?RA`@Az@?L?D?BAF?DCHCFELMNKLKFED[Z"
},
"start_location" :
{
"lat" : 43.0751523,
"lng" : -89.41273889999999
},
"travel_mode" : "DRIVING"
},
{
"distance" :
{
"text" : "0.1 mi",
"value" : 201
},
"duration" :
{
"text" : "1 min",
"value" : 43
},
"end_location" :
{
"lat" : 43.0774459,
"lng" : -89.4159234
},
"html_instructions" : "Continue straight onto \u003cb\u003eElm Dr\u003c/b\u003e",
"maneuver" : "straight",
"polyline" :
{
"points" : "we|eGh`w`PK?GBKBI?I@E?U?uACm@@}A?c@?SA"
},
"start_location" :
{
"lat" : 43.0756416,
"lng" : -89.41588729999999
},
"travel_mode" : "DRIVING"
},
{
"distance" :
{
"text" : "240 ft",
"value" : 73
},
"duration" :
{
"text" : "1 min",
"value" : 21
},
"end_location" :
{
"lat" : 43.0776632,
"lng" : -89.41634990000001
},
"html_instructions" : "Turn \u003cb\u003eleft\u003c/b\u003e\u003cdiv style=\"font-size:0.9em\"\u003eDestination will be on the left\u003c/div\u003e",
"maneuver" : "turn-left",
"polyline" :
{
"points" : "aq|eGn`w`P?R?D@z@?@A@?B?B?@A@AB?@A?A@A@A?A@A?C?A?C?A?AAAAA?AAAAACAE?E?C"
},
"start_location" :
{
"lat" : 43.0774459,
"lng" : -89.4159234
},
"travel_mode" : "DRIVING"
}
],
"traffic_speed_entry" : [],
"via_waypoint" : []
}
],
"overview_polyline" :
{
"points" : "yszeGv}s`PAvC_A?uCAqEGgEOoDGuAIcBSu@?qA?AtBI`LEfMCjXIpDEhBKb@Y`Aw@vDGRAFCHGFQLKBq@?yAAs@?C`FAfDC|AA^Md@MNWTa@`@SBe@DwFAw@A?X?~@AJEFGBI?GCGW"
},
"summary" : "N Park St and University Ave",
"warnings" : [],
"waypoint_order" : []
}
],
"status" : "OK"
}