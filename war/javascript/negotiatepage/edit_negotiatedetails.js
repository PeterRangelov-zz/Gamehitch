$(document).ready(function () {
	var $input = $('.datepicker').pickadate({
		today: false,
		clear: false,
		editable: true,
		format: 'mm/dd/yyyy',
		onSet: function(context) {
	        console.log('Just set stuff:', context);
	    }
	});
	picker = $input.pickadate('date');
	$('#cancellation').autoNumeric('init', {aSign: '$ '});
	$('#amount').autoNumeric('init', {aSign: '$ '});
	
	$.getJSON('//'+location.host+'/rest/negotiation/'+window.location.pathname.replace(/[^\d.,]+/,''), function(data){
		console.log(data);
		
		document.title = data.school1+" vs "+data.school2;
		$('#title').text(data.school1+" vs "+data.school2);
		$('#whenwhere').text(data.games.entry.key+' at '+data.games.entry.value);
		$('#tickets').text(data.tickets);
		$('#comments').text(data.textarea);
		$('.datepicker').val(data.games.entry.key);
		$('#tickets').val(data.tickets);
		if (data.amount){
			$('#amount').autoNumeric('set', data.amount);
		}
		else {
			$('#amount').autoNumeric('set', '0');
		}
		
		$('#cancellation').autoNumeric('set', data.cancellation);
		$('#payer1').next('label').text(data.school1);
		$('#payer2').next('label').text(data.school2);
		$('#payer1').val(data.school1);
		$('#payer2').val(data.school2);
		$('#location1').val(data.games.entry.value);
		$('#location1').next('label').text(data.games.entry.value);
		$('#total').text(accounting.formatMoney(data.amount*1.03));
		
    	if (data.games.entry.value==data.school1){
    		$('#guestText').text(data.school2);
    		$('#locationOtherRadioButton').attr('value', data.school2);
    		$('#location2').val(data.school2);
    		$('#location2').next('label').text(data.school2);
    	}
    	else {
    		$('#guestText').text(data.school1);
    		$('#locationOtherRadioButton').attr('value', data.school1);
    		$('#location2').val(data.school1);
    		$('#location2').next('label').text(data.school1);
    	}
		
    	if (!data.payer) {
    		$('#payer_na').attr('checked', 'checked');
    		$('#amountDiv').hide();
    	}
    	else if (data.payer==$.cookie('rmSchool')){
    		$('#total_title, #total').show();
    		var receiver;
    		if ($.cookie('rmSchool')==data.school1) {
    			receiver=data.school2;
    		}
    		if ($.cookie('rmSchool')==data.school2) {
    			receiver=data.school1;
    		}
    		$('#total').show().append(accounting.formatMoney(data.amount*1.03));
    	}
    	else {
    		$('#total_title, #total').hide();
    	}
    	
	
    	// DETERMINE IF I PAY OR GET PAID
    	if (data.payer==data.school1){
    		$('#payer1').attr('checked', 'checked');
    	}
    	else if (data.payer==data.school2) {
    		$('#payer2').attr('checked', 'checked');
    	}
	});
	
$('#propose').click(function(){
	$.ajax({
        type: 'POST',
        url: '//'+location.host+'/rest/negotiation/'+window.location.pathname.replace(/[^\d.,]+/,'')+'/propose',
        contentType: 'application/json',
        data: {
        	'date': $('.datepicker').val(),
			'location': $('input[name=location]:checked').val(),
        	'payer': $('input[name=payer]:checked').val(),
        	'amount': $('#amount').autoNumeric('get'),
        	'tickets': $('#tickets').val(),
        	'cancellation': $('#cancellation').autoNumeric('get'),
			'comments': $('#comments').val()
        },
        success: function (data, status, jqXHR) {
        	window.location='/negotiations';
        }
	});
	});


	
$('#sign').click(function(){
	$.post('//'+location.host+'/rest/negotiation/'+window.location.pathname.replace(/[^\d.,]+/,'')+'/accept', function(){
		window.location='/negotiations';
	});
});

$('#discard').click(function(){
	window.location='/negotiation/'+window.location.pathname.replace(/[^\d.,]+/,'');
})
$('#delete').click(function(){		
	$.ajax({
		type: 'DELETE',
		url: '//'+location.host+'/rest/negotiation/'+window.location.pathname.replace(/[^\d.,]+/,''),
		success: function (data, status, jqXHR) {window.location='/'}
	});
});
	
$('#payer_na').click(function(){
	$('#amountDiv').hide();
});
$('#payer2, #payer1').click(function(){
	$('#amountDiv').show();
});
	
$('input').on('change', changeButton);
	
$('#amount').on('keyup', function(){
	$('#total').text(accounting.formatMoney($('#amount').autoNumeric('get')*1.03));
})
	
	
	
});
function changeButton(){
	if ($('input[name=payer]:checked').val()==$.cookie('rmSchool')){
		$('#total_title, #total').show();
	}
	else {
		$('#total_title, #total').hide();
	}
}