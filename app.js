const express = require('express');
const bodyParser = require('body-parser');

const sharkController = require('./controllers/sharks');

const app = express();

app.use(bodyParser.urlencoded({ extended: true }));

app.get('/', sharkController.getSharks);
app.post('/sharks', sharkController.addShark);

app.listen(3000, () => {
  console.log('Server running on port 3000');
});
