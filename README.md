# IMDB search app #

### How to start: ####

#### Setup database ####

Application uses PostgreSQL database, it can be setup anywhere, easy way is to use docker image.
Example with docker:

download and run PostgreSQL 9.6:

`docker run --name postgis96 -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d mdillon/postgis:9.6`

After initial download;
stop  : `docker stop postgis96`
start : `docker start postgis96`

#### Start application ####

Set all necessary paths to files, database access points and passwords in file `development.env`.  
*App uses Flyway to create tables in DB.*
 
 Then start app :
 
`source development.env` -- load env variables for app

`sbt run`


### Notes on tasks: ###

Before running any task perform this two actions:
* load data from data set (better if unpacked before loading but will work for gzip also)

`POST http://localhost:8080/api/titles/init` takes ~ 10 min
* crate indexes on db. that will improve speed significantly

`POST http://localhost:8080/api/titles/add_indexes` takes ~ 6 min

#### 1.Top rated movies #### 
Results are sorted by (votesNumber.desc, avgRating.desc) instead fo just avgRating to get more comprehensive results.
Number of results if limited to first 100.

example: 

`GET http://localhost:8080/api/titles/top_rated?genre=Comedy`

#### 2.Typecasting #### 

Two endpoints were crated one with cast member name other with id. Name are ambiguous so bette use id's, cause results
will be more accurate.

example:

`GET http://localhost:8080/api/titles/typecasted?name=Andrew Lincoln`

`GET http://localhost:8080/api/titles/typecasted?id=nm0511088`

#### 3.Find the coincidence #### 
 
Two endpoints were crated one with cast member name other with id. Name are ambiguous so bette use id's, cause results
will be more accurate.

example:

`GET http://localhost:8080/api/titles/coincidence?nameA=Andrew Lincoln&nameB=Norman Reedus`

`GET http://localhost:8080/api/titles/coincidence?idA=nm0511088&idB=nm0005342`

####  4. Kevin Bacon distance #### 

To perform this task i have created two endpoints one for calculating all possible Kevin Bacon distances, and
second for finding distance for particular name or id.
*Distances are calculated for all kind of shows person can participate in : movies, tv series (with episodes), tv shows, shorts etc.*

First generate all KevinBacon distances (as parameter pass Kevin Bacon Id):
https://www.imdb.com/name/nm0000102/?ref_=nv_sr_1?ref_=nv_sr_1

*on my laptop it took around 1.5h*

`POST http://localhost:8080/api/titles/calculate_kevin_bacon_distances?kevinBaconId=nm0000102`

Find distance for actor after calculation finishes:

by name (can return many results cause name are not unique)

`GET http://localhost:8080/api/titles/kevin_bacon_distance?name=Tom Cruise`

by id:

`GET http://localhost:8080/api/titles/kevin_bacon_distance?id=nm0362766`

Results count by kevin bacon distance:

`
select count(*) from cast_members where kevin_bacon_distance = 0; -- 1
select count(*) from cast_members where kevin_bacon_distance = 1; -- 2 200
select count(*) from cast_members where kevin_bacon_distance = 2; -- 258 104
select count(*) from cast_members where kevin_bacon_distance = 3; -- 1 739 188
select count(*) from cast_members where kevin_bacon_distance = 4; -- 1 280 845
select count(*) from cast_members where kevin_bacon_distance = 5; -- 179 621
select count(*) from cast_members where kevin_bacon_distance = 6; -- 19 838
select count(*) from cast_members where kevin_bacon_distance = 7; -- 2 455
select count(*) from cast_members where kevin_bacon_distance = 8; -- 394
select count(*) from cast_members where kevin_bacon_distance = 9; -- 79
select count(*) from cast_members where kevin_bacon_distance = 10; -- 23
select count(*) from cast_members where kevin_bacon_distance = 11; -- 4
`

Person with no connection to Kevin Bacon:

`select count(*) from cast_members where kevin_bacon_distance = -1; -- 5 764 199`



### Further optimization ###

* add compiled Slick queries
* in *Kevin Bacon distance count* bottle neck in database, instead of updating cast_member table with calculated distance,
new table with distances could be crated only with inserts, with append only this would surely improved total calculation time
for given data set. 


### Note on time results ###

All execution times where measure on:

>Linux machine (Xubuntu 16.04)  
>Intel(R) Core(TM) i7-6700HQ CPU @ 2.60GHz  
>32GiB System Memory (2 x 16GiB SODIMM Synchronous 2133 MHz (0,5 ns))  
>Crucial MX300 SSD 275GB M.2
 