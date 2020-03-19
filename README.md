### About
Performs analysis for web server log with records like
```
127.0.0.1 - - [14/06/2017:16:47:02 +1000] "PUT /some/url/address?anyData=values HTTP/1.1" 200 2 44.510983 "-" other-text-of-log
```
grouping them into time intervals by provided signs of unacceptable response with calculation of percent of such responses at this intervals

### Usage
```
$ mvn clean package
$ cat /path/to/access.log | java -jar target/logcheck.jar -u 99.4 -t 45
```
where `-u` is a percent threshold of unacceptable responses and `-t` is a acceptable response time threshold 
