
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



qos_total = "doc['execTimeCore'].value +  doc['execTimeColetor'].value > 2"
qos_core = "doc['execTimeCore'].value > 1"
qos_coletor = "doc['execTimeColetor'].value * 1.68 > 1"

resultado = {
  "size": 0,
  "query": {
    "bool": {
      "filter": [
        {
          "range": {
            "startTimeColetor": {
              "format": "dd-MM-yyyy HH:mm:ss",
              "time_zone" : "America/Sao_Paulo",
              "gte": "07-11-2018 08:00:00",
              "lte": "07-11-2018 20:00:00"
            }
          }
        }
      ]
    }
  },
  "aggs": {
    "confiInicial": {
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
                "source": qos_total
              }
            }
          }
        },
          "core": {
          "filter": {
            "script": {
              "script": {
                "source": qos_core
              }
            }
          }
        },
          "coletor": {
          "filter": {
            "script": {
              "script": {
                "source": qos_coletor
              }
            }
          }
        }
      }
    },
     "confIntermediaria": {
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
                "source": qos_total
              }
            }
          }
        },
          "core": {
          "filter": {
            "script": {
              "script": {
                "source": qos_core
              }
            }
          }
        },
          "coletor": {
          "filter": {
            "script": {
              "script": {
                "source": qos_coletor
              }
            }
          }
        }
      }
    },
     "confOtimizada": {
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
                "source": qos_total
              }
            }
          }
        },
          "core": {
          "filter": {
            "script": {
              "script": {
                "source": qos_core
              }
            }
          }
        },
          "coletor": {
          "filter": {
            "script": {
              "script": {
                "source": qos_coletor
              }
            }
          }
        }
      }
    }
  }
}