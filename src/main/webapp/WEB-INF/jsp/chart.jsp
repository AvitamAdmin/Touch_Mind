<%@ include file="include.jsp" %>
</br>
<h2 style="margin-top:40px;text-align:center;font-weight:bold;" class="mt-10">Dashboard</h2>
								</br>
								</br>
<script type="text/javascript">
	$(document).ready(function() {

	    $("#orderChart").change(function () {
            var key = this.value;
            $.ajax({
            			type : 'GET',
            			headers : {
            				Accept : "application/json; charset=utf-8",
            				"Content-Type" : "application/json; charset=utf-8"
            			},
            			url : '${pageContext.request.contextPath}/chart/data?id=orderSalesApi&key='+ key,
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

        $("#stockChart").change(function () {
                    var key = this.value;
                    $.ajax({
                    			type : 'GET',
                    			headers : {
                    				Accept : "application/json; charset=utf-8",
                    				"Content-Type" : "application/json; charset=utf-8"
                    			},
                    			url : '${pageContext.request.contextPath}/chart/data?id=starcomreport&key='+ key,
                    			success : function(result) {
                    				google.charts.load('current', {
                    					'packages' : [ 'corechart' ]
                    				});
                    				google.charts.setOnLoadCallback(function() {
                    					drawChartStock(result);
                    				});
                    			}
                    		});
            });

            $("#priceChart").change(function () {
                        var key = this.value;
                        $.ajax({
                                    type : 'GET',
                                    headers : {
                                        Accept : "application/json; charset=utf-8",
                                        "Content-Type" : "application/json; charset=utf-8"
                                    },
                                    url : '${pageContext.request.contextPath}/chart/data?id=priceReport-20&key='+ key,
                                    success : function(result) {
                                        google.charts.load('current', {
                                            'packages' : [ 'corechart' ]
                                        });
                                        google.charts.setOnLoadCallback(function() {
                                            drawChartPrice(result);
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
			url : '${pageContext.request.contextPath}/chart/data?id=orderSalesApi&key=site',
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
        				title : 'Order Sales',
        				width : 400,
        				height : 300,
        				is3D: true
        			};
        			var piechart = new google.visualization.PieChart(document
        					.getElementById('orderpiechart_div'));
        			piechart.draw(data, piechart_options);

        			var barchart_options = {
        				title : 'Order Sales',
        				width : 400,
        				height : 300,
        				legend : 'none'
        			};
        			var barchart = new google.visualization.ColumnChart(document
        					.getElementById('orderbarchart_div'));
        			barchart.draw(data, barchart_options);
        		}

		$.ajax({
        			type : 'GET',
        			headers : {
        				Accept : "application/json; charset=utf-8",
        				"Content-Type" : "application/json; charset=utf-8"
        			},
        			url : '${pageContext.request.contextPath}/chart/data?id=starcomreport&key=temSite',
        			success : function(result) {
        				google.charts.load('current', {
        					'packages' : [ 'corechart' ]
        				});
        				google.charts.setOnLoadCallback(function() {
        					drawChartStock(result);
        				});
        			}
        		});

        		function drawChartStock(result) {

                			var data = new google.visualization.DataTable();
                			data.addColumn('string', 'Site');
                			data.addColumn('number', 'Count');
                			var dataArray = [];
                			$.each(result, function(key, value) {
                				dataArray.push([ key, value ]);
                			});

                			data.addRows(dataArray);

                			var piechart_options = {
                				title : 'Stock Report',
                				width : 400,
                				height : 300,
                				is3D: true
                			};
                			var piechart = new google.visualization.PieChart(document
                					.getElementById('stockpiechart_div'));
                			piechart.draw(data, piechart_options);

                			var barchart_options = {
                				title : 'Stock Report',
                				width : 400,
                				height : 300,
                				legend : 'none'
                			};
                			var barchart = new google.visualization.ColumnChart(document
                					.getElementById('stockbarchart_div'));
                			barchart.draw(data, barchart_options);
                		}

		$.ajax({
                			type : 'GET',
                			headers : {
                				Accept : "application/json; charset=utf-8",
                				"Content-Type" : "application/json; charset=utf-8"
                			},
                			url : '${pageContext.request.contextPath}/chart/data?id=priceReport-20&key=temSite',
                			success : function(result) {
                				google.charts.load('current', {
                					'packages' : [ 'corechart' ]
                				});
                				google.charts.setOnLoadCallback(function() {
                					drawChartPrice(result);
                				});
                			}
                		});

                		function drawChartPrice(result) {

                        			var data = new google.visualization.DataTable();
                        			data.addColumn('string', 'Site');
                        			data.addColumn('number', 'Count');
                        			var dataArray = [];
                        			$.each(result, function(key, value) {
                        				dataArray.push([ key, value ]);
                        			});

                        			data.addRows(dataArray);

                        			var piechart_options = {
                        				title : 'Price Report',
                        				width : 400,
                        				height : 300,
                        				is3D: true
                        			};
                        			var piechart = new google.visualization.PieChart(document
                        					.getElementById('pricepiechart_div'));
                        			piechart.draw(data, piechart_options);

                        			var barchart_options = {
                        				title : 'Price Report',
                        				width : 400,
                        				height : 300,
                        				legend : 'none'
                        			};
                        			var barchart = new google.visualization.ColumnChart(document
                        					.getElementById('pricebarchart_div'));
                        			barchart.draw(data, barchart_options);
                        		}

	});
</script>

<table class="columns">
                                   <div>
                                        <tr>
                                        <div class="col-sm-3 cheil-select" >
                                            <select id="orderChart" name="orderChart" class="3col active">
                                                <option value="site">Order Sales by Site</option>
                                                <option value="Status">Order Sales by Status</option>
                                                <option value="returnRequestedApi-Status">Return Order by Status</option>
                                                <option value="returnRequestedApi-Refund Amount">Return Order by Refund Amount</option>
                                            </select>
                                        </div>
                                            <td><div id="orderpiechart_div" ></div></td>
                                            <td><div id="orderbarchart_div" ></div></td>
                                        </tr>
                                    </div>
                                    </table>
                                    </br>
                                    <table class="columns">
                                    <div>
                                        <tr>
                                        <div class="col-sm-3 cheil-select" >
                                            <select id="stockChart" name="stockChart" class="3col active">
                                                <option value="temSite">Stock by Site</option>
                                                <option value="stockLevel">Stock by Stock Level</option>
                                                <option value="stockLevelStatus">Stock by Status</option>
                                            </select>
                                        </div>
                                            <td><div id="stockpiechart_div" ></div></td>
                                            <td><div id="stockbarchart_div" ></div></td>
                                        </tr>
                                    </div>
                                    </table>
                                    </br>
                                    <table class="columns">
                                    <div>
                                        <tr>
                                        <div class="col-sm-3 cheil-select" >
                                            <select id="priceChart" name="priceChart" class="3col active">
                                                <option value="temSite">Price Report by Site</option>
                                                <option value="discount">Price Report by Discount</option>
                                                <option value="price">Price Report by Price</option>
                                                <option value="promotionPrice">Price Report by PromoPrice</option>
                                            </select>
                                        </div>
                                            <td><div id="pricepiechart_div" ></div></td>
                                            <td><div id="pricebarchart_div" ></div></td>
                                        </tr>
                                    </div>
                                </table>