<%@ include file="../include.jsp" %>
</br>
<h2 style="margin-top:40px;text-align:center;font-weight:bold;" class="mt-10">Dashboard</h2>
								</br>
								</br>
<script type="text/javascript">
	$(document).ready(function() {

	    $("#qaChart").change(function () {
            var key = this.value;
            $.ajax({
            			type : 'GET',
            			headers : {
            				Accept : "application/json; charset=utf-8",
            				"Content-Type" : "application/json; charset=utf-8"
            			},
            			url : '${pageContext.request.contextPath}/chart/testdata?key='+ key,
            			success : function(result) {
            				google.charts.load('current', {
            					'packages' : [ 'corechart' ]
            				});
            				google.charts.setOnLoadCallback(function() {
            					drawChart(result);
            				});
            			}
            		});
        });

		$.ajax({
			type : 'GET',
			headers : {
				Accept : "application/json; charset=utf-8",
				"Content-Type" : "application/json; charset=utf-8"
			},
			url : '${pageContext.request.contextPath}/chart/testdata?key=byStatus',
			success : function(result) {
				google.charts.load('current', {
					'packages' : [ 'corechart' ]
				});
				google.charts.setOnLoadCallback(function() {
					drawChart(result);
				});
			}
		});

				function drawChart(result) {

        			var data = new google.visualization.DataTable();
        			data.addColumn('string', 'Site');
        			data.addColumn('number', 'Count');
        			var dataArray = [];
        			$.each(result, function(key, value) {
        				dataArray.push([ key, value ]);
        			});

        			data.addRows(dataArray);

        			var piechart_options = {
        				title : 'Test Results',
        				width : 400,
        				height : 300,
        				is3D: true
        			};
        			var piechart = new google.visualization.PieChart(document
        					.getElementById('qapiechart_div'));
        			piechart.draw(data, piechart_options);

        			var barchart_options = {
        				title : 'Test Results',
        				width : 400,
        				height : 300,
        				legend : 'none'
        			};
        			var barchart = new google.visualization.ColumnChart(document
        					.getElementById('qabarchart_div'));
        			barchart.draw(data, barchart_options);
        		}


	});
</script>

<table class="columns">
                                   <div>
                                        <tr>
                                        <div class="col-sm-3 cheil-select" >
                                            <select id="qaChart" name="qaChart" class="3col active">
                                                <option value="byStatus">Test results By Status</option>
                                                <option value="byTestName">Test results By Name</option>
                                            </select>
                                        </div>
                                            <td><div id="qapiechart_div" ></div></td>
                                            <td><div id="qabarchart_div" ></div></td>
                                        </tr>
                                    </div>
                                    </table>
                                    </br>

                                </table>