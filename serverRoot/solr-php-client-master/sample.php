<?php
// make sure browsers see this page as utf-8 encoded HTML
include 'SpellCorrector.php';

header('Content-Type: text/html; charset=utf-8');
$limit = isset($_REQUEST['limit'])? $_REQUEST['limit'] : 10;
$query = isset($_REQUEST['q']) ? $_REQUEST['q'] : false;
$additionalParameters = [
	//'fl' => isset($_REQUEST['fl'])? $_REQUEST['fl'] : '',
    'fl' => 'title, og_description, id,  og_url',
	'sort' => isset($_REQUEST['sort']) && $_REQUEST['sort'] == 'pageRankFile' ? 'pageRankFile desc': '',
    //'sort' => 'pageRankFile desc'
];
$results = false;
if ($query)
{
 // The Apache Solr Client library should be on the include path
 // which is usually most easily accomplished by placing in the
 // same directory as this script ( . or current directory is a default
 // php include path entry in the php.ini)
 require_once('Apache/Solr/Service.php');
 // create a new solr service instance - host, port, and corename
 // path (all defaults in this example)
    
 $solr = new Apache_Solr_Service('localhost', 8983, '/solr/myexample/');
 
    
 // if magic quotes is enabled then stripslashes will be needed
 if (get_magic_quotes_gpc() == 1)
 {
 $query = stripslashes($query);
 }
 // in production code you'll always want to use a try /catch for any
 // possible exceptions emitted by searching (i.e. connection
 // problems or a query parsing error)
 try
 {
 $results = $solr->search($query, 0, $limit,$additionalParameters);
 //$results = $solr->search('content:blah', 0, 10, array('sort' => 'timestamp desc'));
 }
 catch (Exception $e)
 {
 // in production you'd probably log or email this error to an admin
 // and then show a special message to the user but for this example
 // we're going to show the full exception
 die("<html><head><title>SEARCH EXCEPTION</title><body><pre>{$e->__toString()}</pre></body></html>");
 }
}
?>
<html>
 <head>
 <title>PHP Solr Client Example</title>
 </head>
 <body>
 <form accept-charset="utf-8" method="get">
<table>
<tr>
<td>Search:</td>
<td><input type="text" name="q" value="<?php echo htmlspecialchars($query, ENT_QUOTES, 'utf-8'); ?>"></td>
</tr>
<tr>
<td>Limit:</td>
<td><input type="number" name="limit" min = "1" value="<?php echo htmlspecialchars($limit, ENT_QUOTES, 'utf-8'); ?>"></td>
</tr>
<tr>
<td>
<div style="float:left">
  <input type="radio" name="sort" value="" <?php if($additionalParameters['sort'] == '' ){echo checked;} ?> >Lucene<br>
  <input type="radio" name="sort" value="pageRankFile" <?php if($additionalParameters['sort'] != '' ){echo checked;} ?>>Page Rank<br>
</div>
</td>
</tr>
</table>
<input type="submit"/>
</form>
    
<?php
// display results
if ($results)
{
 $total = (int) $results->response->numFound;
 $start = min(1, $total);
 $end = min($limit, $total);
?>
<div>Results <?php echo $start; ?> - <?php echo $end;?> of <?php echo $total; ?>:</div>
<ol>
<?php
 // iterate result documents
 foreach ($results->response->docs as $doc)
 {
?>
 <li>
 <table style="text-align: left">
   
 <tr>
    <th><a href=<?php echo $doc->og_url ?> ><?php echo htmlspecialchars($doc->title, ENT_NOQUOTES, 'utf-8');?></a></th>
 <tr>
 <tr>
    <th><a href=<?php echo $doc->og_url ?> ><?php echo htmlspecialchars($doc->og_url, ENT_NOQUOTES, 'utf-8');?></a></th>
 <tr>
 <tr>
    <td><?php echo htmlspecialchars($doc->id, ENT_NOQUOTES, 'utf-8');?></td>
 <tr>    
 <tr>
    <td><?php echo htmlspecialchars($doc->og_description, ENT_NOQUOTES, 'utf-8');?></td>
 <tr>


 <!--    
<?php
 // iterate document fields / values
 foreach ($doc as $field => $value)
 {
?>
 <tr>
 <th><?php echo htmlspecialchars($field, ENT_NOQUOTES, 'utf-8'); ?></th>
 <td><?php echo htmlspecialchars($value, ENT_NOQUOTES, 'utf-8'); ?></td>
 </tr>
<?php
 }
?>
    -->
 </table>
 </li>
<?php
 }
?>
 </ol>
<?php
}
?>
 </body>
</html>
