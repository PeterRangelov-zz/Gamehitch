$(document).ready(function () {
    $('#dateFrom, #dateTo').datepicker();
   events = {
       	url: '/rest/event',
    	data: {
            distance: $('#distance').val(),
            home: $('#homeCheckbox').is(':checked'),
            away: $('#awayCheckbox').is(':checked'),
            conferences: JSON.stringify($('input:checkbox:checked.conf').map(function () {
      		  return this.value;
    		}).get())
        },
    	error: function(){
    		console.log('error');
    	}
    };
    
    $('#calendarDiv').fullCalendar({
    	loading: function (bool){
    		$('#loading').toggle(bool);
    	},
    	events: events,
        contentHeight: 800,
        contentWidth: '100%',
        theme: true,
        allDayDefault: true,
        weekMode: 'variable',
        header: {
            left: 'prev,next today',
            center: 'title',
            right: 'month, basicWeek, basicDay'
        },
        month: 9,
        dayClick: function( date, allDay, jsEvent, view ){
        	$('#calendarDiv').fullCalendar('changeView', 'basicDay');
        	$('#calendarDiv').fullCalendar('gotoDate', date);
        },
        eventClick: function(calEvent, jsEvent, view) {
        	$('#modal').foundation('reveal', 'open');
        	$('#modalDate').val(moment(calEvent.start).format('MM/DD/YYYY'));
        	$('#modalOpponent').val(calEvent.school);
        	if (calEvent.location=='away'){
        		$('#modalLocation').val($.cookie('rmSchool'));
        	}
        	else {
        		$('#modalLocation').val(calEvent.location);
        	}
        	$('#modalPayerMe').attr('value', $.cookie('rmSchool'));
        	$('#modalPayerThem').attr('value', calEvent.school);
        	
        	
        	console.log(calEvent);
        }
    });
    
    $('body').keyup(function(e){
		if (e.keyCode == 37) {$('#calendarDiv').fullCalendar('prev');}
		if (e.keyCode == 39) {$('#calendarDiv').fullCalendar('next');}
	});
    

    // TRIGGERS FOR REFETCH
    $('#distance').change(refetch);
    $('input[type=checkbox]').change(refetch);
   
    $('#uncheckAllConferences').click(function(){
    	$('#checkboxSpan > label > input[type=checkbox]').prop('checked', false);
    	refetch();
    });
    
    $('#checkAllConferences').click(function(){
    	$('#checkboxSpan > label > input[type=checkbox]').prop('checked', true);
    	refetch();
    });
 
    
    
    $('#homeCheckboxLabel').append($.cookie('rmSchool'));
    
    
    
    

    
    
    
    // MODAL STUFF
    $('#modalPayerNobody').click(function(){
		$('#dollarAmount').hide();
	});
	$('#modalPayerMe, #modalPayerThem').click(function(){
		$('#dollarAmount').show();
	});
	$('#close_modal').click(function(){
		$('#modal').foundation('reveal', 'close');
	})
    
    $('#negotiate').click(function(){
    	// AJAX POST TO CREATE NEW NEGOTIATION
    	$.ajax({
	         type: 'PUT',
	         url: '//'+location.host+'/rest/negotiation',
	         contentType: 'application/json',
	         data: {
	             'date': $('#modalDate').val(),
	             'opponent': $('#modalOpponent').val(),
	             'amount': $('#dollarAmount').autoNumeric('get'),
	             'payer': $('input[name=payer]:checked').val(),
	             'location': $('#modalLocation').val(),
	             'tickets': $('#tickets').val(),
	             'cancellation': $('#cancellation').autoNumeric('get'),
	             'comments': $('#comments').val()
	         },
	         success: function (data, status, jqXHR) {
	        	 $('#modal').foundation('reveal', 'close');
	         },
	         error: function (jqXHR, status) {console.log(status);}
	     }); 
    });
    
    $('#dollarAmount').autoNumeric('init', {aSign: '$ '});
    $('#cancellation').autoNumeric('init', {aSign: '$ '});
});

function refetch(){
	
	$('#calendarDiv').fullCalendar( 'removeEventSource', events );
	events = {
	    	url: '/rest/event',
	    	data: {
	            distance: $('#distance').val(),
	            home: $('#homeCheckbox').is(':checked'),
	            away: $('#awayCheckbox').is(':checked'),
	            conferences: JSON.stringify($('input:checkbox:checked.conf').map(function () {
	      		  return this.value;
	    		}).get())
	        },
	    	error: function(){
	    		console.log('error');
	    	}
	};
	    	
	$('#calendarDiv').fullCalendar('addEventSource', events);
	
}