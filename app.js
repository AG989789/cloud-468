const express = require('express');
const bodyParser = require('body-parser');
const path = require('path');

const app = express();

require('./db');

const sharkController = require('./controllers/sharks');

app.use(bodyParser.urlencoded({ extended: true }));

app.use(express.static(path.join(__dirname, 'views')));

app.get('/', sharkController.index);
app.get('/sharks', sharkController.getSharks);
app.post('/sharks', sharkController.addShark);

app.listen(3000, () => {
  console.log('Server running on port 3000');
});
