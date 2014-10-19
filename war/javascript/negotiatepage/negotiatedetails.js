$(document).ready(function () {
	
	
	$.getJSON('//'+location.host+'/rest/negotiation/'+window.location.pathname.replace(/[^\d.,]+/,''), function(data){
		console.log(data);
		
		document.title = data.school1+" vs "+data.school2;
		$('#title').text(data.school1+" vs "+data.school2);
		$('#whenwhere').text(data.games.entry.key+' at '+data.games.entry.value);
		$('#tickets').text(data.tickets);
		$('#cancellation').text(accounting.formatMoney(data.cancellation));
		$('#comments').text(data.textarea);
		$('#tickets').val(data.tickets);
		
	
		
    	if (data.games.entry.value==data.school1){
    		$('#guestText').text(data.school2);
    		$('#locationOtherRadioButton').attr('value', data.school2);
    	}
    	else {
    		$('#guestText').text(data.school1);
    		$('#locationOtherRadioButton').attr('value', data.school1);
    	}
		
    	if (!data.payer) {
    		$('#payment').text('No payment for this game');
    	}
    	else if (data.payer==$.cookie('rmSchool')){
    		$('#total').show();
    		var receiver;
    		if ($.cookie('rmSchool')==data.school1) {
    			receiver=data.school2;
    		}
    		if ($.cookie('rmSchool')==data.school2) {
    			receiver=data.school1;
    		}
    		$('#payment').text($.cookie('rmSchool')+' pays '+receiver+' '+accounting.formatMoney(data.amount));
    		$('#total').show().append(accounting.formatMoney(data.amount*1.03));
    	}
    	else if (data.payer!=$.cookie('rmSchool')){
    		$('#payment').text($.cookie('rmSchool')+' receives '+accounting.formatMoney(data.amount));
    	}
	
    	
	});
	



	
$('#sign').click(function(){
	$.post('//'+location.host+'/rest/negotiation/'+window.location.pathname.replace(/[^\d.,]+/,'')+'/accept', function(){
		window.location='/negotiations';
	});
});
$('#edit').click(function(){
	window.location='/negotiation/edit/'+window.location.pathname.replace(/[^\d.,]+/,'')
})

$('#delete').click(function(){		
	$.ajax({
		type: 'DELETE',
		url: '//'+location.host+'/rest/negotiation/'+window.location.pathname.replace(/[^\d.,]+/,''),
		success: function (data, status, jqXHR) {window.location='/'}
	});
});

	
	
	
});