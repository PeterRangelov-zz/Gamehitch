$(document).ready(function(){
	$('#phoneNumber').formance('format_phone_number');
	
	$('#updateButton').click(updateInfo);
});

function updateInfo(){
	$.ajax({
        type: 'POST',
        url: '//'+location.host+'/rest/accountpage/update',
        contentType: 'application/json',
        data: {
            'firstName': $('#firstName').val(),
            'lastName': $('#lastName').val(),
            'phoneNumber': $('#phoneNumber').val(),
            'emailAddress': $('#emailAddress').val(),
        },
        success: function (data, status, jqXHR) {
        	$.notify('Success! Your changes are saved.', 'success');
        	},
        error: function (jqXHR, status) {$.notify('Unable to save', 'error');}
   });
}