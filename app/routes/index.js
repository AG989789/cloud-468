const express = require('express');
const router = express.Router();
const Shark = require('../models/sharks');

// GET: display sharks
router.get('/', async (req, res) => {
  const sharks = await Shark.find();
  res.render('index', { sharks });
});

// POST: add shark
router.post('/add', async (req, res) => {
  const newShark = new Shark({
    name: req.body.name
  });

  await newShark.save();
  res.redirect('/');
});

module.exports = router;
