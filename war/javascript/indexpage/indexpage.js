$(document).ready(function(){
	$.notify.addStyle('happyblue', {
		  html: "<div>" +
		  		"<span data-notify-text/>" +
		  		"</div>",
		  classes: {
		    base: {
		      "white-space": "nowrap",
		      "background-color": "#B23A00",
		      "padding": "15px",
		      "color": "white"
		    }
		  }
		});
	
	
		$('#getEarlyAccess1, #getEarlyAccess2, #getEarlyAccess3').click(function(){
			console.log('clicked');
			$('#modal').modal('show');
		});
		
		
		
		$('#submitButton').click(function(){
			$('#modal').modal('hide');
			$('#getEarlyAccess1, #getEarlyAccess2, #getEarlyAccess3').hide();
			$.notify('Thanks!', {
      		  style: 'happyblue',
      		  className: 'base'
      		});
			$.ajax({
		         type: 'POST',
		         url: '//'+location.host+'/rest/earlyaccess',
		         contentType: 'application/json',
		         data: {
		             'email': $('#email').val(),
		             'college': $('#college').val(),
		             'sport': $('#sport').val(),
		             'ip': myp
		             
		         },
		         success: function (data, status, jqXHR) {
		        	 
		         },
		         error: function (jqXHR, status) {
		             console.log(status);
		        	
		         }
		     }); 
		});
		
		
		
		
});


  