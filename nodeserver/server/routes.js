
///contains all post requests api
// var base64Img = require('base64-img');
// var fs = require('fs');

var fs = require('fs');
module.exports = function (app, db) {

    //test endpoint
    app.get('/', (req, res) => {
        res.send("Server is working!!");
    });

    app.post('/getCars', (req, res) => {
        db.query("Select * from cars inner join model ON cars.model_no= model.model_no\
		 				where  maxTimePeriod >0 and loc_lng is not null \
		 				and loc_lat is not null", function (err, result, fields) {
            if (err) {
                console.log(err.sqlMessage);
                res.json({ result: "fail" });
            }
            else {
                console.log(result);
                var car = result;
                res.json({ result: car });
            }
        });
    });
    app.post('/reg', (req, res) => {
        var name = req.body.name;
        var contact = req.body.mobileno;
        var city = req.body.city;
        var email = req.body.email;
        var pass = req.body.password;

        var sql = 'INSERT INTO users(name,contact,city,email,password) \
					   			  VALUES(?,?,?,?,?)';

        db.query(sql, [name, contact, city, email, pass], (err, result) => {
            if (err) {
                console.log(err.sqlMessage);
                res.json({ message: "0" });
            } else {
                console.log("Registration successfull");
                res.json({ message: "1" });
            }
        })
    });

    app.post('/regDriver', (req, res) => {
        var name = req.body.name;
        var contact = req.body.mobileno;
        var city = req.body.city;
        var email = req.body.email;
        var pass = req.body.password;

        var sql = 'INSERT INTO driver(name,contact,city,email,password) \
					   			  VALUES(?,?,?,?,?)';

        db.query(sql, [name, contact, city, email, pass], (err, result) => {
            if (err) {
                console.log(err.sqlMessage);
                res.json({ message: "0" });
            } else {
                console.log("Registration successfull");
                res.json({ message: "1" });
            }
        })
    });

    //Login
    app.post('/login', (req, res) => {
        var u_name = req.body.email;
        var sql = "SELECT password FROM users WHERE email = ?";

        db.query(sql, [u_name], (err, result) => {
            if (err)
                console.log(err.sqlMessage);
            else {
                if (result.length > 0) {
                    console.log("Password sent successfully");
                    res.json({ password: result[0].password });
                }
                else {
                    console.log("User not found");
                    res.json({ password: "Fail" });
                }
            }
        })
    })
    app.post('/loginDriver', (req, res) => {
        var u_name = req.body.email;
        var sql = "SELECT password FROM driver WHERE email = ?";

        db.query(sql, [u_name], (err, result) => {
            if (err)
                console.log(err.sqlMessage);
            else {
                if (result.length > 0) {
                    console.log("Password sent successfully");
                    res.json({ password: result[0].password });
                }
                else {
                    console.log("User not found");
                    res.json({ password: "Fail" });
                }
            }
        })
    })
    app.post('/reqOwner', (req, res) => {
        searchQuery = JSON.parse(req.body.searchQuery);
        selectedCar = JSON.parse(req.body.selectedCar);
        console.log(searchQuery.address + " " + selectedCar.regNo);
        var city;
        var pin;
        var addr;
        var lng;
        var lat;
        var dlng;
        var dlat;

        city = searchQuery.city;
        addr = searchQuery.address;
        pin = searchQuery.pincode;
        lng = searchQuery.lng;
        lat = searchQuery.lat;
        addLocation(db, lng, lat, city, addr, pin);

        city = req.body.descity;
        addr = req.body.desaddress;
        pin = req.body.despincode;
        dlng = req.body.deslng;
        dlat = req.body.deslat;
        addLocation(db, dlng, dlat, city, addr, pin);

        var sd, ed, ts, carid, userid;
        ts = req.body.timestamp
        sd = searchQuery.startDate;
        ed = searchQuery.endDate;
        carid = selectedCar.regNo;
        userid = "atulpatare99@gmail.com"

        var sql = 'INSERT INTO transaction(car_id,user_id,startdate_ts,\
						enddate_ts,datetime_ts,sl_lng,sl_lat,dl_lng,dl_lat) \
					   			  VALUES(?,?,?,?,?,?,?,?,?)';

        db.query(sql, [carid, userid, sd, ed, ts, lng, lat, dlng, dlat], (err, result) => {
            if (err) {
                console.log(err.sqlMessage);
                res.json({ message: "0" });
            } else {
                var sql = 'update cars set booking = 1 where regNo = ?';

                db.query(sql, [carid], (err, result) => {
                    if (err) {
                        console.log(err.sqlMessage);
                        res.json({ message: "0" });
                    } else {
                        console.log("Request successfull");
                        res.json({ message: "1" });
                    }
                })
            }
        })

        // console.log(req.body);
        // res.json({message:"1"});
    })
    app.post('/getStatus', (req, res) => {
        console.log(req.body);
        var regno = req.body.regNo;
        var sql = "select  booking from cars where regNo = ?";
        db.query(sql, [regno], (err, result) => {
            if (err) {
                console.log(err.sqlMessage);
                res.json({ message: -1 });
            } else {
                console.log("Status returned = " + result[0].booking);
                res.json({ message: result[0].booking });
            }
        })
    })
    app.post('/assignDriver', (req, res) => {
        console.log(req.body);
        var ts = req.body.datetime;
        var sql = "select driver_id from transaction where datetime_ts= ? "
        db.query(sql, [ts], (err, result) => {
            if (err) {
                console.log(err.sqlMessage);
                res.json({ message: "server error" });
            } else {
                console.log(result);
                var driver_id = result[0].driver_id;
                console.log("Status returned = " + driver_id);
                if (driver_id) {
                    console.log("driver already assigned");
                    res.json({ message: "driver already assigned" });
                }
                else {
                    var city = req.body.city;
                    console.log(req.body)
                    var sql = "Select * from driver where transaction_id is NULL AND city = ?";
                    db.query(sql, [city], (err, result) => {
                        if (err) {
                            console.log(err.sqlMessage);
                            res.json({ message: err.sqlMessage });
                        }
                        else if (result.length == 0) {
                            console.log("No drivers available");
                            res.json({ message: "No drivers available" });
                        }
                        else {
                            console.log(result);
                            driver_id = result[0].email;
                            res.json({ message: result[0] })
                            var sql = "update driver set transaction_id = ? where email = ?";
                            db.query(sql, [ts, driver_id], (err, result) => {
                                if (err) {
                                    console.log(err.sqlMessage);
                                } else {
                                    console.log("Driver Status updated  = " + driver_id);
                                }
                            })
                            var sql = "update transaction set driver_id = ? where datetime_ts = ?";
                            db.query(sql, [driver_id, ts], (err, result) => {
                                if (err) {
                                    console.log(err.sqlMessage);
                                } else {
                                    console.log("Driver Status updated  = " + driver_id);
                                }
                            })
                        }
                    })

                }
            }
        })
    })
    app.post('/getFinalTransactionDetails', (req, res) => {
        console.log(req.body);
        var ts = req.body.datetime;

        var sql = "select  * from transaction where datetime_ts = ?";
        db.query(sql, [ts], (err, result) => {
            if (err) {
                console.log(err.sqlMessage);
                res.json({ message: -1 });
            } else {
                console.log("Status returned = " + result[0]);
                res.json(result[0]);
            }
        })
    })

    app.post('/getFinalTransactionDetailsOwner', (req, res) => {
        console.log(req.body);
        var car_id = req.body.car_id;

        var sql = "select  * from transaction where datetime_ts = \
				(select  max(datetime_ts) from transaction where car_id = ?)";
        db.query(sql, [car_id], (err, result) => {
            if (err) {
                console.log(err.sqlMessage);
                res.json({ message: -1 });
            } else {
                console.log("Status returned = " + result[0]);
                res.json(result[0]);
            }
        })
    })

    app.post('/dummyOwner', (req, res) => {
        console.log(req.body);
        var regid = req.body.regNo;
        var status = req.body.status;
        var sql = 'update cars set booking = ? where regNo = ?';

        db.query(sql, [status, regid], (err, result) => {
            if (err) {
                console.log(err.sqlMessage);
                res.json({ message: "0" });
            } else {
                console.log("Request successfull");
                res.json({ message: "1" });
            }
        })
    })
    app.post('/driverUpdateStatus', (req, res) => {
        console.log(req.body);
        var regid = req.body.regNo;
        var status = req.body.ChangeStatus;
        var sql = 'update cars set booking = ? where regNo = ?';

        db.query(sql, [status, regid], (err, result) => {
            if (err) {
                console.log(err.sqlMessage);
                res.json({ message: "0" });
            } else {
                console.log("Request successfull");
                res.json({ message: "1" });
            }
        })


    })
    app.post('/driverUpdateStatus2', (req, res) => {
        console.log(req.body);
        var regid = req.body.regNo;
        var ts = req.body.ts;
        var status = req.body.ChangeStatus;
        var sql = 'update cars set booking = ? where regNo = ?';

        db.query(sql, [status, regid], (err, result) => {
            if (err) {
                console.log(err.sqlMessage);
            } else {
                console.log("Request successfull");
            }
        })
        sql = "select  * from transaction where datetime_ts = ?";
        db.query(sql, [ts], (err, result) => {
            if (err) {
                console.log(err.sqlMessage);
                res.json({ message: -1 });
            } else {
                console.log("Status returned = " + result[0]);
                res.json(result[0]);
            }
        })
    })
    app.post('/driverUpdateTransac', (req, res) => {
        console.log(req.body);
        var total_cost = req.body.totalCost;
        var ts = req.body.ts;
        var driver_cost = req.body.driverCost;
        var car_cost = req.body.carCost;
        var dist = req.body.dist;
        var sql = 'update transaction set total_cost = ?,driver_cost = ?,car_cost = ?\
												,journey_distance = ?\
												 where datetime_ts = ?';

        db.query(sql, [total_cost, driver_cost, car_cost, dist, ts], (err, result) => {
            if (err) {
                console.log(err.sqlMessage);
                res.json({ message: "0" });
            } else {
                console.log("Request successfull");
                res.json({ message: "1" });
            }
        })
    })
    app.post('/getTransactionDetail', (req, res) => {
        var u_email = req.body.email;
        var sql = 'select transaction.car_id ,transaction.datetime_ts,location.city \
						from transaction\
						inner join location on transaction.sl_lat=location.latitude\
											and transaction.sl_lng=location.longitude\
						where transaction.user_id = ?';

        db.query(sql, [u_email], (err, result) => {
            if (err) {
                console.log(err.sqlMessage);
                res.json({ message: "0" });
            }
            else if (result.length == 0) {
                console.log("user has no rides yet");
                res.json({ message: "You have no rides yet" });
            }
            else {
                console.log(result);
                res.json({ message: "transactions fetched", data: result })
            }
        })
    })
    app.post('/getMyCars', (req, res) => {
        console.log(req.body);
        var u_email = req.body.email;
        var sql = 'select * from cars where owner_id = ?';

        db.query(sql, [u_email], (err, result) => {
            if (err) {
                console.log(err.sqlMessage);
                res.json({ message: "error" });
            }
            else if (result.length == 0) {
                console.log("user has no cars ");
                res.json({ message: "You have not uploaded any car yet" });
            }
            else {
                console.log("cars fetched" + result);
                res.json({ message: "cars fetched", data: result });
            }
        })
    })
    app.post('/model', (req, res) => {
        console.log(req.body);
        var sql = 'select name,model_no from model';

        db.query(sql, (err, result) => {
            if (err) {
                console.log(err.sqlMessage);
                res.json({ message: "error" });
            } else {
                console.log("Request successfull");
                res.json({ data: result });
            }
        })
    })

    function addLocation(db, lng, lat, city, addr, pin) {
        var sql = 'INSERT INTO location(longitude,latitude,city,address,pincode) \
					   			  VALUES(?,?,?,?,?)';

        db.query(sql, [lng, lat, city, addr, pin], (err, result) => {
            if (err) {
                console.log(err.sqlMessage);
            } else {
                console.log("location added");
            }
        })
    }
    app.post('/getAssignment', (req, res) => {
        var email = req.body.email;
        var sql =
            'select transaction.*,u1.name as \'user_name\',u2.name as \'owner_name\',\
		 driver.rate_perday,\
		 cars.* from transaction\
		 inner join cars on transaction.car_id = cars.regNo\
		 inner join driver on transaction.driver_id = driver.email\
		 inner join users as u1 on transaction.user_id = u1.email\
	 	 inner join users as u2 on u2.email=(select owner_id from cars where regNo=transaction.car_id)\
		 where transaction.datetime_ts=\
		 (select transaction_id from driver where email=?);'

        db.query(sql, [email], (err, result) => {
            console.log(result);
            if (err) {
                console.log(err.sqlMessage);
                res.json({ message: "error" });
            }
            else if (result.length == 0) {
                res.json({ message: "null" });

            }
            else {
                console.log("Request successfull");
                res.json({ message: "success", data: result[0] });
            }
        })
    })
    app.get('/getImage', function (req, res) {
        console.log(req.query.regNo);
        var name = req.query.regNo;
        res.sendFile(__dirname + '/images/' + name.toString() + '.jpg');
    });
    app.post('/owner', (req, res) => {
        console.log("car owner");
        var model_no = req.body.model_no;
        var fair = req.body.fair;
        var nod = req.body.nod;
        var img = req.body.image;
        var email = req.body.email;//new one
        var city = req.body.city;
        var lng = req.body.loc_lng;
        var lat = req.body.loc_lat;
        var addr = req.body.address;
        var pincode = req.body.pincode;
        var reg_no = req.body.regNo;

        addLocation(db, lng, lat, city, addr, pincode);

        console.log(reg_no);
        var sql = "INSERT INTO cars (regNo,owner_id, maxTimePeriod,rating,rate_km,\
			  city,image,model_no,loc_lat,loc_lng,verified,booking) \
			  VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        db.query(sql, [reg_no, email, nod, 0, fair, city, reg_no, model_no, lat, lng, false, 0],
            function (err, result) {
                if (err) {
                    console.log("FAILED::" + err.sqlMessage);
                    res.json({ message: "Failed" })
                }
                else {
                    console.log("1 record inserted");
                    fs.writeFileSync(__dirname + '/images/' + reg_no + '.jpg', img, 'base64', function (err) {
                        if (err) { console.log("ERROR" + err); }
                        else
                            res.json({ message: "Failed" })
                    });
                    res.json({ message: "Success" });
                    console.log("image Recieved");

                }
            });

        var sql = "insert into owner (email) values(?) "
        db.query(sql, [email], function (err, result) {
            if (err) {
                console.log("FAILEDDDD:: " + err.sqlMessage);
            }
            else {
                console.log("owner added");
            }
        });
    });
};


// 'select transaction.*,cars.loc_lng,cars.loc_lat,cars.regNo from transaction\
//  inner join cars on transaction.car_id = cars.regNo\
//  where transaction.datetime_ts=\
//  (select transaction_id from driver where email=?);'


 // 'select transaction.*,u1.name as 'user_name',u2.name = 'owner_name',\
 // cars.loc_lng,cars.loc_lat,cars.regNo from transaction\
 // inner join cars on transaction.car_id = cars.regNo\
 // inner join users as u1 on transaction.user_id = u1.email\
 // inner join users as u2 on u2.email=(select owner_id from cars where regNo=transaction.car_id)\
 // where transaction.datetime_ts=\
 // (select transaction_id from driver where email='driver1@gmail.com');'

 // 'select transaction.*,u1.name as 'user_name',u2.name as 'owner_name', \
 // cars.loc_lng,cars.loc_lat,cars.regNo,cars.owner_id  from transaction\
 // inner join cars on transaction.car_id = cars.regNo\
 // inner join users as u1 on transaction.user_id = u1.email\
 // inner join users as u2 on u2.email='mayuripawar238@gmail.com'\
 // where transaction.datetime_ts=\
 // (select transaction_id from driver where email='driver1@gmail.com');'


 // 'select * from cars transaction.datetime_ts\
	// 	INNER JOIN transaction on car.regNo =\
	// 	 (select  (transaction.datetime_ts) as \'datetime_ts\' where car_id=cars.regNo)\
	// 	 where owner_id = ?'



	// 'select transaction.*,u1.name as 'user_name',u2.name as 'owner_name',\
	// 	 driver.rate_perday,\
	// 	 cars.* from transaction\
	// 	 inner join cars on transaction.car_id = cars.regNo\
	// 	 inner join driver on transaction.driver_id = driver.email\
	// 	 inner join users as u1 on transaction.user_id = u1.email\
	//  inner join users as u2 on u2.email=(select owner_id from cars where regNo=transaction.car_id)\
	// 	 where transaction.datetime_ts=\
	// 	 (select transaction_id from driver where email=?);'

	// select transaction.*,u1.name as 'user_name',u2.name as 'owner_name',\
	// 	 driver.rate_perday,\
	// 	 cars.* from transaction\
	// 	 inner join cars on transaction.car_id = cars.regNo\
	// 	 inner join driver on transaction.driver_id = driver.email\
	// 	 inner join users as u1 on transaction.user_id = u1.email\
	//  inner join users as u2 on u2.email=(select owner_id from cars where regNo=transaction.car_id)\
	// 	 where transaction.datetime_ts=\
	// 	 (select transaction_id from driver where email='driver1@gmail.com');