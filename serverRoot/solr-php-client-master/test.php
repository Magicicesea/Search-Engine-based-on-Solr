<?php
include 'SpellCorrector.php';

ini_set('display_errors', 'On');
error_reporting(E_ALL);

echo SpellCorrector::correct('octabr');
//it will output *october*
?>
