$(document).ready(function(){
	
	$('#newUserForm').keyup(function(){
		if ($('#newUserForm').parsley('isValid')) {
			$('#newUserFormButton').attr('disabled', false);
		}
		else {
			$('#newUserFormButton').attr('disabled', true);
		}
	});
	
	$('#loginForm').keyup(function(e){
		if (e.keyCode == 13) {signin();}
	});
	
	$('#newUserForm').keyup(function(e){
		if (e.keyCode == 13) {register();}
	});
	
	
	
	
	
	$('#loginFormButton').click(signin);
	
	$('#newUserFormButton').click(register);
	
	$('#loginLabel').click(function(){
		$('#passwordResetForm').hide();
		$('#loginForm').show();
	});
	
	
	$('#passwordResetLabel').click(function(){
		$('#loginForm').hide();
		$('#passwordResetForm').show();
	});
	
	$('#passwordResetFormButton').click(function(){
		$.ajax({
	        type: 'POST',
	        url: '//'+location.host+'/rest/registration/sendpasswordresetlink',
	        contentType: 'application/json',
	        data: {
	        	'baseURL': 'http://'+location.host,
	            'emailAddress': $('#passwordResetFormEmailAddress').val(),
	            'activationToken': Math.floor((Math.random()*999999999999999)+1),
	            'activationTokenExpires': moment().add('days', 2).format('YYYY-MM-DD')
	        },
	        success: function (data, status, jqXHR) {
	        	console.log(data)
	        	if (data.success){
	        		$('#passwordResetForm, #newUserForm').hide();
		        	$('#h4Notification, #h5Notification').show();
		       		$.notify('Please check your email', 'success');
	        	}
	        	else {$('#passwordResetFormButton').notify(data.message, 'error');}
	        	
	        },
	        error: function (jqXHR, status) {
	            console.log(status);
	        }
		});
	});
	
	
	
  
  
  
});

function register(){
	if ($('#newUserForm').parsley('isValid')) {
		  $.ajax({
		         type: 'POST',
		         url: '//'+location.host+'/rest/registration/user',
		         contentType: 'application/json',
		         data: {
		        	 'baseURL': 'http://'+location.host,
		             'emailAddress': $('#emailTextbox').val().toLowerCase(),
		             'password': $('#passwordTextbox').val(),
		             'activationToken':  Math.floor((Math.random()*999999999999999)+1),
		             'activationTokenExpires': moment().add('days', 10).format('YYYY-MM-DD')
		         },
		         success: function (data, status, jqXHR) {
		        	 if (data.success){
		        		 $('#container').hide();
		        		 $('#registerNotification').show();
			             console.log('user added');
		        	 }
		        	 else {
		        		 $('#emailTextbox').notify(data.message, 'error');
		        	 }
		         },
		         error: function (jqXHR, status) {
		             console.log('error adding user');
		         }
		     }); 
	  }
}

function signin(){
	$.ajax({
         type: 'POST',
         url: '//'+location.host+'/rest/registration/login',
         contentType: 'application/json',
         data: {
             'emailAddress': $('#loginFormEmail').val().toLowerCase(),
             'password': $('#loginFormPassword').val(),
             'date': moment().format('YYYY-MM-DD')
         },
         success: function (data, status, jqXHR) {
        	 if (data.success){
        		 console.log(data);
        		 if (data.role=='Athletic Director') {
        			 console.log("AD");
        			 window.location='/adportal';
        			 }
        		 if (data.role=='Coach') {
        			 console.log("NOT AD");
        			 window.location = '/search';
        			 }
	        	 
        	 }
        	 else {
        		 $('#loginFormButton').notify(data.message, 'error');
        	 }
        	 
         },
         error: function (jqXHR, status) {
             console.log(status);
         }
     }); 
}

