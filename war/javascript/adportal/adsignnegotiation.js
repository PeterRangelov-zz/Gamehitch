$(document).ready(function(){
		console.log('sdfdf');
	
		$.getJSON('//'+location.host+'/rest/negotiation/'+window.location.pathname.replace(/[^\d.,]+/,''), function(data){
			console.log(data);
			$('#title').text(data.school1+' vs '+data.school2);
			$('#date').text(data.games.entry.key);
			$('#location').text(data.games.entry.value);
			if (data.payer){
				$('#payer').text(data.payer+ ' pays');
			}
			if (data.amount){
				$('#amount').text(accounting.formatMoney(data.amount));
			}
			
			
			$('#tickets').text(data.tickets);
			$('#cancellation').text(accounting.formatMoney(data.cancellation));
			$('#comments').text(data.textarea);
			
			if (data.payer==$.cookie('rmSchool')){
				$('#total').show();
				$('#total').append(accounting.formatMoney(data.amount*1.03));
			}
			
		} );
		
		$('#sign').click(function(){
			$('#delete, #sign').prop('disabled', true);
			$('#spinner').show();
			$.post('//'+location.host+'/rest/negotiation/'+window.location.pathname.replace(/[^\d.,]+/,'')+'/sign', function(result){
					window.location='/adportal';
				});
		});
		
		$('#delete').click(function(){
			$.ajax({
		        type: 'DELETE',
		        url: '//'+location.host+'/rest/negotiation/'+window.location.pathname.replace(/[^\d.,]+/,''),
		        success: function (data, status, jqXHR) {window.location='/adportal'}
		   });
			
			
		});
		
		
});