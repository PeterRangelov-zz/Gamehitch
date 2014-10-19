$(document).ready(function(){
	// check the stage of Enter Billing Information Process
	$.getJSON('//'+location.host+'/rest/payment/status', function( data ) {
	       	 console.log(data)
	       	 if (data.phase==0){
	       		 $('#enterACH').show();
	       	 }
	       	 if (data.phase==1){
	       		$('#verifyACH, #currentACH').show();
	       		$('#statusH5').append(data.verification_status);
	       		$('#banknameTD').text(data.bank_name);
	       		$('#accountnumberTD').text(data.account_number);
	       		$('#routingnumberTD').text(data.routing_number);
	       		$('#accountnameTD').text(data.account_name);
	       		$('#accounttypeTD').text(data.account_type);
	       		$('#accountNameTD').text(data.account_name);
	       	 }
	       	if (data.phase==2){
	       		$('#currentACH').show();
	       		$('#statusH5').append(data.verification_status);
	       		$('#banknameTD').text(data.bank_name);
	       		$('#accountnumberTD').text(data.account_number);
	       		$('#routingnumberTD').text(data.routing_number);
	       		$('#accountnameTD').text(data.account_name);
	       		$('#accounttypeTD').text(data.account_type);
	       		$('#accountNameTD').text(data.account_name);
	       	 }
	        
	    });
	
//	$('#ba-name').val('John Doe');
//    $('#ba-number').val('9900000000');
//    $('#ba-routing').val('321174851');
//    $('#deposit1').val('1');
//    $('#deposit2').val('1');
    
	
	$('#ba-submit').click(function (e) {
		$('#ba-submit').prop('disabled', true);
		$('#ba-submit_spinner').show();
		  e.preventDefault();
		  var payload = {
		    name: $('#ba-name').val(),
		    routing_number: $('#ba-routing').val(),
		    account_number: $('#ba-number').val()
		  };

		  // Create bank account
		  balanced.bankAccount.create(payload, handleResponse);
		});
	
	
    $('#verify').click(function () {
    	$('#verify').prop('disabled', true);
    	$('#verify_spinner').show();
	    $.ajax({
	        type: 'POST',
	        url: '//'+location.host+'/rest/payment',
	        contentType: 'application/json',
	        data: {
	            'deposit1': $('#deposit1').val(),
	            'deposit2': $('#deposit2').val()
	        },
	        success: function (data, status, jqXHR) {
	       	 console.log('success! '+data)
	       	 
	       	 // SHOW CONFIRMATION
	       	$('#verifiedNotification').show();
	       	$('#verifyACH').hide();
	       	$('#verify_spinner').hide();
	       	 
	        },
	        error: function (jqXHR, status) {
	            console.log(status);
	            $.notify('Verification failed');
	            $('#verify_spinner').hide();
	        	$('#verify').attr('disabled', false);
	        }
	    }); 
    });
});

function handleResponse(response) {
	console.log('handleResponse called. Status: '+response.status_code);
	console.log(response);
	
	  if (response.status_code === 201) {
	    // Call your backend
	    $.ajax({
	         type: 'PUT',
	         url: '//'+location.host+'/rest/payment',
	         contentType: 'application/json',
	         data: {
	             'uri': response.bank_accounts[0].href
	         },
	         success: function (data, status, jqXHR) {
	        	 console.log(data)
	        	
	        	 
	         },
	         error: function (jqXHR, status) {
	             console.log(status);
	        	
	         },
	         complete: function (){
	        	 $.notify('Your information has been submitted to Gamehitch', 'success')
	        	 $('#enterACH').hide();
	        	 $('#verifyACH').show();
	        	 $('#ba-submit_spinner').hide();
	        	 $('#ba-submit').attr('disabled', false);
	         }
	     }); 
	    
	}
	  
	  if (response.status_code != 201) {
		  $.notify(response.errors[0].description);
		  $('#ba-submit_spinner').hide();
     	 $('#ba-submit').attr('disabled', false);
	  }
}
