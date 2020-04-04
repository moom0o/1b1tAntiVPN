const express = require("express");
const fs = require("fs");
const app = express();
app.use(express.urlencoded());

app.get("/", (request, response) => {
  response.sendFile(__dirname + "/views/index.html");
});
var multer = require("multer");
var upload = multer();
app.post("/addname", upload.none(), (request, response) => {
  console.log(request.body);
  var unirest = require("unirest");
  var req = unirest(
    "POST",
    `https://www.google.com/recaptcha/api/siteverify?secret=SECRETHERE&response=${
      request.body["g-recaptcha-response"]
    }`
  ).end(function(res) {
    if (res.error) console.log(res.raw_body);

    if (JSON.parse(res.raw_body).success === true) {
      var unirest = require('unirest');
      var req = unirest('GET', `https://api.mojang.com/users/profiles/minecraft/${request.body["username"].replace("(", "").replace(")", "")}`)
        .end(function (res) { 
          if (res.error){
            console.log(res.error)
            response.status(400).send("Invalid username")
            return
          } else{
            console.log("no error")
            if(!res.raw_body.includes("id")){
              response.status(400).send("Invalid username")
              return
            } else {
              let names = JSON.parse(fs.readFileSync(`/DIR/names.json`, "utf-8"));
              array = names;
              array.push(request.body["username"].replace("(", "").replace(")", ""));
              fs.writeFileSync(`/DIR/names.json`, JSON.stringify(array, null, 4), err => {
                if (err) throw err;
              });
              response.send(
                "Name added! Try relogging. If it still doesn't work contact the owner."
              );
            }
          }
        });
    } else {
      response.status(403).send("Google Recaptcha failed!");
    }
  });
});

app.get("/names", (request, response) => {
  let names = JSON.parse(fs.readFileSync(`/DIR/names.json`, "utf-8"));
  response.send(names);
});

// listen for requests :)
const listener = app.listen(3002, () => {
  console.log("Your app is listening on port " + 3002);
});
