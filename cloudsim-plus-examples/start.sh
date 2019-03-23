#!/bin/bash
nohup java -jar target/cloudsim-plus-examples-3.0.1.jar 2 2 um14 um16 > logs/um22.out &
nohup java -jar target/cloudsim-plus-examples-3.0.1.jar 2 3 um14 um16 > logs/um23.out;
nohup java -jar target/cloudsim-plus-examples-3.0.1.jar 2 3 dois14 dois16 > logs/dois23.out &
nohup java -jar target/cloudsim-plus-examples-3.0.1.jar 2 3 tres14 tres16 > logs/tres23.out &