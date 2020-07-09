// main server code
const express    = require('express');
const bodyParser = require('body-parser');
const app        = express();
const mysql      = require('mysql');

// mysql connection
const con = mysql.createConnection({
  host: "localhost",
  user: "root",
  password: "root",
  database: "carrental",
  charset : "utf8mb4"
});


con.connect(function(err) {
  if (err) throw err;
  console.log("DB Connected!");
});

// middlewares
// parse application/x-www-form-urlencoded && application/json
app.use(bodyParser.urlencoded({ extended: true, limit: '50mb' }))
app.use(bodyParser.json({limit: '50mb'}))


require('./routes')(app, con);

const port = process.env.PORT || 8000
app.listen(port, () => {
  console.log('We are live on port = ' + port);
});