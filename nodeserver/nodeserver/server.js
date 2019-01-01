
//main server code

const express        = require('express');
//const MongoClient    = require('mongodb').MongoClient;
const bodyParser     = require('body-parser');
const app            = express();

const mysql = require('mysql');

const con = mysql.createConnection({
  host: "localhost",
  user: "root",
  password: "Hrs@mysql123",
  database: "carrental"
});

con.connect(function(err) {
  if (err) throw err;
  console.log("Connected!");
});

app.use(bodyParser.json({ extended: true ,limit: '50mb'}));
app.use(bodyParser.urlencoded({limit: '50mb'}));
require('./app/routes')(app, con);

const port = 8000;
app.listen(port, () => {
  console.log('We are live on ' + port);
});
