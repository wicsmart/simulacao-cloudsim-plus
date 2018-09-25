GET bigdata/_search
{
"query": {"match_all": {}}  
}


GET bigdata/_search
{
   "size": 5,
    "query": {
        "bool": {
            "filter": [
                {
                    "term": {
                        "nome.keyword": "testeDinamico"
                    }
                }
                ]
        }
    }
}











GET bigdata/_delete_by_query
{
    "query": {
        "bool": {
            "filter": [
                {
                    "term": {
                        "nome.keyword": "teste"
                    }
                }
                ]
        }
    }
}



GET bigdata/_search
{"size": 0, 
  "aggs": {
    "names": {
      "terms": {
        "field": "nome.keyword",
        "size": 20
      }
    }
  }
}


GET bigdata/_search
{
  "query": {"bool": {"filter": {"exists": {
    "field": "carga_dados"
  }}}}
}


DELETE bigdata

PUT bigdata
{
  "settings": {
            "number_of_shards": 1,
            "number_of_replicas": 1
        },
        "mappings": {
            "_doc": {
                "properties": {
                    "created": {
                        "type":   "date",
                        "format": "yyyy/MM/dd HH:mm:ss||epoch_millis"
                    },
                    "startTimeColetor": {
                        "type":   "date",
                        "format": "strict_hour_minute_second_millis"
                    },
                    "startTimeCore": {
                        "type":   "date",
                        "format": "strict_hour_minute_second_millis"
                    },
                    "duracao": {
                        "type":   "date",
                        "format": "strict_hour_minute_second_millis"
                    },
                    "timeUsage": {
                        "type":   "date",
                        "format": "strict_hour_minute_second_millis"
                    }
                }
            }
        }
    }
