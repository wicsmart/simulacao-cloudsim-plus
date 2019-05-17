
logs = {
  "size": 0,
  "query": {
    "bool": {
      "filter": [
        {
          "range": {
            "timestamp": {
              "format": "dd-MM-yyyy HH:mm:ss",
              "gte": "07-11-2018 18:00:01",
              "lte": "07-11-2018 18:02:00"
            }
          }
        }
      ]
    }
  },
  "aggs": {
    "serie": {
      "date_histogram": {
        "field": "timestamp",
        "interval": "1s"
      },
      "aggs": {
        "pontos": {
          "sum": {
            "field": "pontos"
          }
        }
      }
    }
  }
}

resultado = {
   "size": 0,
  "query": {
    "bool": {
      "filter": [
        {
          "range": {
            "startTimeColetor": {
              "format": "dd-MM-yyyy HH:mm:ss",
              "gte": "07-11-2018 10:00:00",
              "lte": "07-11-2018 22:00:00"
            }
          }
        }
      ]
    }
  },
  "aggs": {
    "c42xc42x": {
      "filter": {
        "term": {
          "nome.keyword": "c42xc42xdez22"
        }
      },
      "aggs": {
        "quantidade": {
          "filter": {
            "script": {
              "script": {
                "source": "doc['execTimeCore'].value +  doc['execTimeColetor'].value> 3"
              }
            }
          }
        }
      }
    },
    "c4xc44x": {
      "filter": {
        "term": {
          "nome.keyword": "c4xc44xdez22"
        }
      },
      "aggs": {
        "quantidade": {
          "filter": {
            "script": {
              "script": {
                "source": "doc['execTimeCore'].value +  doc['execTimeColetor'].value> 3"
              }
            }
          }
        }
      }
    },
     "c42x-c42xc44xsim": {
      "filter": {
        "term": {
          "nome.keyword": "c42x-c42xc44xsim"
        }
      },
      "aggs": {
        "quantidade": {
          "filter": {
            "script": {
              "script": {
                "source": "doc['execTimeCore'].value +  doc['execTimeColetor'].value> 3"
              }
            }
          }
        }
      }
    },
     "c42xc4x-c42xc44xsim": {
      "filter": {
        "term": {
          "nome.keyword": "c42xc4x-c42xc44xsim"
        }
      },
      "aggs": {
        "quantidade": {
          "filter": {
            "script": {
              "script": {
                "source": "doc['execTimeCore'].value +  doc['execTimeColetor'].value> 3"
              }
            }
          }
        }
      }
    }
  }
}