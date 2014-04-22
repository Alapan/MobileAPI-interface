<?php error_reporting(E_ALL);
        $response=file_get_contents('https://graph.facebook.com/me?fields=id&access_token='.$_GET['accesstoken']);  // Send access token to Facebook graph to determine validity
        $obj=json_decode($response);

        if($obj->id){                      // This id is returned only if the access token is valid

                if(isset( $_GET["latitude"], $_GET["longitude"])){
                        $result=file_get_contents('http://maps.googleapis.com/maps/api/geocode/json?latlng='.$_GET["latitude"].','.$_GET["longitude"].'&sensor=false');
                        echo $result;
                }
                else{
                        echo "Location details not received";
                }
        }else{
                echo "User login is invalid!";
        }
?>


