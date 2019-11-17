const express = require("express");
const path = require('path')

const PORT = process.env.PORT || 5000

var app = express();

var rockStatus = "stopped";

app.listen(PORT, () => {
    console.log("Server running on port " + PORT);
});

app.route("/kabachok/rock")
    .get((req, res) => {
        res.send(rockStatus);
    });

// for simplicity - gets
app.route("/kabachok/rock/start")
    .get((req, res) => {
        rockStatus = "started";
        res.send();
    });

app.route("/kabachok/rock/stop")
    .get((req, res) => {
        rockStatus = "stopped";
        res.send();
    });