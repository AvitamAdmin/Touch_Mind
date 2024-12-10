<%@ include file="../include.jsp" %>
<c:set var="issueCount" value="${qaSummaryData.issuesChartMap.size()}" scope="page" />

<c:set var="topMargin" value="42%" scope="page" />

<style>
.maincanva{
display: flex;
            flex-direction: row;
            width:100%;
}
.tabledatas{
table-layout:auto;
width:100%
}
.flex-item{
display: flex;
            flex-direction: row;
               gap: 10px;
}
.content {
            display: flex;
            flex-direction: row;

        }
    table {
        border-collapse: collapse;
        width: 100%;
    }
    th, td {
        border: 1px solid black;
        padding: 8px;
        text-align: left;
    }
    .hover-canva{
            transition:transform.2s;
            }
            .hover-canva:hover{
         background-color:#ebebeb;
                        }
    .hover-item{
        transition:transform.2s;
        }
        .hover-item:hover{
        transform: scale(1.1)
        }.hover-item{
         transition:transform.2s;
         }
         .hover-item:hover{
         transform: scale(1.2)
         }
         .subsidiary{
         border: 2px solid red,
         border-radius:20px
         }
         .rounded-box0{
         width:15px;
         height:15px;
         background-color:#32b466;
         border-radius:10px;}
     .rounded-box1{
              width:15px;
                       height:15px;
              background-color:#e62525;
              border-radius:10px;}
      .rounded-box2{
               width:15px;
                        height:15px;
               background-color:#ff9900;
               border-radius:10px;}
               .canva{
               display: flex; flex-direction: row; gap: 50px;
               }
       .rounded-box3{
          width:15px;
                   height:15px;
          background-color:#12e29f;
          border-radius:10px;}
          .canva{
          display: flex; flex-direction: row; gap: 50px;
      }
      .rounded-box4{
        width:15px;
                 height:15px;
        background-color:#2181a1;
        border-radius:10px;}
        .canva{
        display: flex; flex-direction: row; gap: 50px;
    }
    .rounded-box5{
            width:15px;
                     height:15px;
            background-color:#e5fe84;
            border-radius:10px;}
            .canva{
            display: flex; flex-direction: row; gap: 50px;
        }
    .rounded-box6{
            width:15px;
                     height:15px;
            background-color:#720f20;
            border-radius:10px;}
            .canva{
            display: flex; flex-direction: row; gap: 50px;
        }
    .rounded-box7{
            width:15px;
                     height:15px;
            background-color:#f1bd02;
            border-radius:10px;}
            .canva{
            display: flex; flex-direction: row; gap: 50px;
        }
        .rounded-box8{
                    width:15px;
                             height:15px;
                    background-color:#7ffd4f;
                    border-radius:10px;}
                    .canva{
                    display: flex; flex-direction: row; gap: 50px;
                }
        .rounded-box9{
                    width:15px;
                             height:15px;
                    background-color:#84840f;
                    border-radius:10px;}
                    .canva{
                    display: flex; flex-direction: row; gap: 50px;
                }
    .rounded-box10{
                width:15px;
                         height:15px;
                background-color:#267db6;
                border-radius:10px;}
                .canva{
                display: flex; flex-direction: row; gap: 50px;
            }
            @media only screen and (min-width:1240px){

            .donut1{
            justify-content:space-evenly;
            background-color:lightgreen;
            }
            }
            .donut{
            position: absolute;
                                            top: ${topMargin};
                                            left: 29px;
                                            width: 63px;
                                            height: 63px;
                                            background: #fff;
                                            border-radius: 60px;
                                            justify-content: center;
                                            align-items: center;
                                            display: flex;
                                            flex-direction: column;}
</style
<div class="main-content">
<div class="row">
    <div class="col-sm-12">
        <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
    </div>
</div>
</br>
</br>



<div style="display: flex;flex-direction: row;width:100%;"><h3 style="font-weight: bolder;">${dashboard.identifier}</h3></div>

</br>
</br>
  <div class="row" style="padding: 0 0 10px 0;margin-left:5px;margin-right:5px">
    <div class="col-sm-4" style="padding:6px;">
         <select class="cheil-select subsidiary" style="background: #fff;border:2px solid #7f7f7f;border-radius: 20px;" multiple name="subsidiary" id="subsidiaryId" placeholder="Select Subsidiary" >
             <span>Select Subsidiary</span>
                <c:forEach items="${subsidiaries}" var="child">
                   <c:choose>
                       <c:when test="${fn:contains(subsidiaryId, child.identifier)}">
                         <option value="${child.id}" selected>${child.identifier}</option>
                       </c:when>
                       <c:otherwise>
                          <option value="${child.id}" style="font-weight:bold;padding:2px">${child.identifier}</option>
                       </c:otherwise>
                   </c:choose>
                </c:forEach>
         </select>
    </div>
    <div class="col-sm-4">
             <select class="cheil-select" name="days" style="background: #fff;border:2px solid #7f7f7f;border-radius: 20px;" id="dateRange" placeholder="Today" >
                 <span>Select Date Range</span>
                 <option value="0" selected>Last Run</option>
                 <option value="1" <c:if test="${days==1}"> selected</c:if>>Last 24h</option>
                 <option value="3" <c:if test="${days==3}"> selected</c:if>>Last 3 days</option>
                 <option value="7" <c:if test="${days==7}"> selected</c:if>>Last 7 days</option>
             </select>
        </div>
        <div class="col-sm-4" style="margin-top:4px;">
                 <select class="cheil-select" name="runner" style="background: #fff;border:2px solid #7f7f7f;border-radius: 20px;"  id="runner" placeholder="All The runners" multiple>
                     <span>Select Runner</span>
                     <option value="" style="font-weight:bold">All the runners</option>
                        <c:forEach items="${qaSummaryData.users}" var="child">
                           <c:choose>
                               <c:when test="${fn:contains(runner, child)}">
                                 <option value="${child}" selected>${child}</option>
                               </c:when>
                               <c:otherwise>
                                  <option value="${child}" style="font-weight:bold;padding:2px">${child}</option>
                               </c:otherwise>
                           </c:choose>
                        </c:forEach>
                 </select>
            </div>
  </div>
</br>
  <div style="border:2px solid #7e7e7e;width:100%">
  </div>
</br>

<div class="row maincanva" >
    <div class='content col-sm-4 hover-canva donut1' style='background: #fff;width:100%;padding:10px'>

            <div style="width:40%;">
            <div>
                <h6 style="font-weight: bolder;">Test Cases</h6>
                </div>
<div style="width:100px;"><canvas id="myChart" width="150" height="150" style=" display: block; position: relative;"></canvas>
                <div class="donut" >
                <h6>Total</h6>
                <h4 style="font-weight: bolder;">${qaSummaryData.totalTestCaseCount}</h4>
                </div>
                </div>
            </div>
            <div class="flex-column" style="background-color:white;width:60%;justify-content:center;align-items:flex-start;display:flex;flex-direction:column">
            <c:forEach items="${qaSummaryData.testCasesMap}" var="testCaseMap" varStatus="loop">
                <div class='flex-item ' style="width:80%;justify-content:space-between;display: flex;flex-direction: row;">
                    <div class='flex-item'><p class="rounded-box${loop.index}"></p>
                                             <h6>${testCaseMap.key}</h6></div>
                                             <div><h6>${testCaseMap.value}%</h6></div>
                </div>
            </c:forEach>
            </div>
            </div>


    <div class='content col-sm-4 hover-canva donut1' style='background: #fff;width:100%;padding:10px'>

            <div style="width:40%;">
            <div style=" display: flex;flex-direction: row;gap:5px;justify-content: center;align-items: center;background: #fff;
">
                <div style="display: flex;flex-direction: row;gap:5px;justify-content: center;align-items: center;"><div style="font-weight: bolder;">Total SKUs</div></div>
                <c:if test="${not empty qaSummaryData.skuErrorMapList}">
                <div class="hover-item">
                 <c:if test="${not empty qaSummaryData.skuErrorMapList}">
                     <form method="POST" id="qaSummaryData" enctype="multipart/form-data" action="/qa/downloadFailedSkus" class="handle-upload" modelAttribute="qaSummaryData" >
                                       <input name="skuErrorMapList" value="${qaSummaryData.skuErrorMapList}" style="display:none" type="hidden"/>
                                        <button  aria-controls="tableData" type="submit" title="Download" style="border:none;"><i class="fa fa-download" aria-hidden="true"></i></button>
                                        </form>
                 </c:if>

                </div>
                </c:if>

                </div>
                <div style="width:100px;"><canvas id="myChart2" width="150" height="150" style=" display: block; position: relative;"></canvas>
                <div class="donut" >
                <h6>Total</h6>
                <h4 style="font-weight: bolder;">${qaSummaryData.totalSkuCount}</h4>
                </div>
                </div>

            </div>
            <div class="flex-column" style="background-color:white;width:60%;justify-content:center;align-items:flex-start;display:flex;flex-direction:column">
            <c:forEach items="${qaSummaryData.skusMap}" var="skuMap" varStatus="loop">
                <div class='flex-item ' style="width:80%;justify-content:space-between;display: flex;flex-direction: row;">
                    <div class='flex-item'><p class="rounded-box${loop.index}"></p>
                                             <h6>${skuMap.key == 'Partial' ? 'Partially passed' : skuMap.key}</h6></div>
                                             <div><h6>${skuMap.value}%</h6></div>
                </div>
                </c:forEach>
            </div>
        </div>


<c:if test="${not empty qaSummaryData.issuesChartMap}">
    <div class='content col-sm-4 hover-canva donut1' style='background: #fff;width:100%;padding:10px'>

            <div style="width:40%;">
                    <div>
                        <h6 style="font-weight: bolder;">Issues</h6>
                        </div>
<div style="width:100px;"><canvas id="myChart3" width="150" height="150" style=" display: block; position: relative;"></canvas>
               <div class="donut" >
                <h6>Total</h6>
                <h4 style="font-weight: bolder;">${qaSummaryData.totalIssuesCount}</h4>
                </div>
                </div>
                    </div>
                                <div class="flex-column" style="background-color:white;width:60%;justify-content:center;align-items:flex-start;display:flex;flex-direction:column;max-height:220px;max-height:220px;overflow:auto;scrollbar-width:none">

                    <c:forEach items="${qaSummaryData.issuesChartMap}" var="issueMap" varStatus="loop">
                <div class='flex-item ' style="width:80%;justify-content:space-between;display: flex;flex-direction: row;">
                            <div class='flex-item'><p class="rounded-box${loop.index}"></p>
                                                     <h6>${issueMap.key}</h6></div>
                                                     <div><h6>${issueMap.value}%</h6></div>
                        </div>
                        </c:forEach>
                    </div>
                </div>
    </div>
    </c:if>
</div>

</br>
  <div style="border:2px solid #7e7e7e;width:100%">
  </div>
</br>

<div style="width:100%,background-color:#fff">

    <c:forEach items="${qaSummaryData.issuesMap}" var="issueMap">
<c:set var="count" value="0" scope="page" />
            <div style="display: flex;flex-direction: row;width:100%;"><h4 style="font-weight: bolder;">Issues per category on ${issueMap.key}</h4></div>
</br>
<c:set var="mapCount" value="0" scope="page" />
<c:forEach items="${issueMap.value}" var="valueMap">
   <c:set var="mapCount" value="${mapCount + 1}" scope="page"/>
</c:forEach>
<c:forEach items="${issueMap.value}" var="valueMap">
    <c:if test="${count==0 || count % 4 == 0}">
         <div style="width:100%;background-color:#fff;display: flex;flex-direction: row;gap:20px">
                 <div class="" style="background: #fff;border:1px solid #7f7f7f;border-radius: 6px;padding:10px;gap:15px;display: flex;flex-direction: column;min-height:240px;max-height:240px;max-width:23%;min-width:23%;overflow:auto;">
             </c:if>
             <c:if test="${count!=0 && count % 4 != 0}">
                 <div class="" style="background: #fff;border:1px solid #7f7f7f;border-radius: 6px;padding:10px;gap:15px;display: flex;flex-direction: column;min-height:240px;max-height:240px;max-width:23%;min-width:23%;overflow:auto;">
             </c:if>
            <div style="display: flex;flex-direction: row;width:100%;justify-content:space-between;align-items:center">
            <div><h4 style="font-weight: bolder;">${valueMap.key}</h4></div>
            <c:set var="errorCount" value="${valueMap.value.size()}"/>
            <c:if test="${errorCount <= 3}">
            <div><img style="width:25px;" src="${contextPath}/images/material-symbols_startGreen.png"/></div>
            </c:if>
            <c:if test="${errorCount > 3 && errorCount <= 6}">
            <div><img style="width:25px;" src="${contextPath}/images/material-symbols_start-Orange.png"/></div>
            </c:if>
            <c:if test="${errorCount > 6}">
                        <div><img style="width:25px;" src="${contextPath}/images/material-symbols_start.png"/></div>
                        </c:if>
            </div>
            <div>

            <c:forEach items="${valueMap.value}" var="value">
            <div><h6 style="color:#4d4d4d">${value}</h6></div>
            </c:forEach>
            </div>
            <c:set var="count" value="${count + 1}" scope="page"/>
            <c:if test="${count % 4 != 0}">
             </div>

             </c:if>
             <c:if test="${count % 4 == 0 || mapCount==count}">
                 </div>
                 </div>
                 </br>
                  </br>
             </c:if>
            </c:forEach>
         </c:forEach>
</div>
<hr>
<div style="display: flex;flex-direction: row;width:100%;"><h4 style="font-weight: bolder;">Top 10 failed/partially failed testcases</h4></div>
</br>
<div class="row">
    <div class="col-sm-12">
         <table style="table-layout:fixed;max-width:100%">
              <thead>
                    <tr>
                      <th class="">Subsidiaries</th>
                      <th class="">Test cases</th>
                      <th class="">Status</th>
                      <th class="">Passed SKUs</th>
                      <th class="">Impact</th>
                    </tr>
              </thead>
              <tbody>
                  <c:forEach items="${qaSummaryData.failedData}" var="model">
                  <c:set var="color" value="green" />
                  <c:if test="${model.impact >=50 && model.impact <= 75}">
                    <c:set var="color" value="orange" />
                  </c:if>
                  <c:if test="${model.impact >75}">
                      <c:set var="color" value="red" />
                  </c:if>
                  <tr style="color: ${color};">
                        <td class="td-sm">${model.subsidiary}</td>
                        <td class="td-sm" style="word-wrap: break-word;">${model.testCase}</td>
                        <td class="td-sm">${model.status}</td>
                        <td class="td-sm">${model.passedSkus}</td>
                        <td class="td-sm">${model.impact}</td>
                  </tr>
                 </c:forEach>
             </tbody>
         </table>
    </div>
  </div>
</br>
</br>
<div style="display: flex;flex-direction: row;width:100%;"><h4 style="font-weight: bolder;">Result based on Subsidiary</h4></div>
</br>
<div class="row">
    <div class="col-sm-12">
         <table width="100%" style="table-layout: fixed;">
              <thead>
                    <tr>
                      <th class="">Subsidiaries</th>
                      <th class="">Site</th>
                      <th class="">Passed</th>
                      <th class="">Failed</th>
                    </tr>
              </thead>
              <tbody>
                  <c:forEach items="${qaSummaryData.subsidiaryData}" var="model">
                        <td class="td-sm">${model.subsidiary}</td>
                        <td class="td-sm">${model.site}</td>
                        <td class="td-sm">${model.passedSkus}</td>
                        <td class="td-sm">${model.failedSkus}</td>
                     </tr>
                 </c:forEach>
             </tbody>
         </table>
    </div>
  </div>
</br>
</br>

  <div style="display: flex;flex-direction: row;width:100%;"><h4 style="font-weight: bolder;">Issue overview of last 7 days</h4></div>
<div class="row">
          <canvas id="stackedChartID" width="1200" height="600" style="width:1100px;height:600px"></canvas>
      </div>

<script language="javascript">

$(document).ready(function () {
         var multipleCancelButton = new Choices('#subsidiaryId', {
             removeItemButton: true,
             maxItemCount:-1,
             searchResultLimit:20,
             renderChoiceLimit:-1,
             placeholder: true,
             placeholderValue: "Select Subsidiaries"
           });
   var multipleCancelButton = new Choices('#dateRange', {
                removeItemButton: true,
                maxItemCount:-1,
                searchResultLimit:20,
                renderChoiceLimit:-1,
                placeholder: true,
                placeholderValue: "Select Range"
              });
  var multipleCancelButton = new Choices('#runner', {
               removeItemButton: true,
               maxItemCount:-1,
               searchResultLimit:20,
               renderChoiceLimit:-1,
               placeholder: true,
               placeholderValue: "Select Runner"
             });
    $("#subsidiaryId").unbind('change');
    //$("#subsidiaryId").change(function(){
    $(document).on("change", "#subsidiaryId", function(e){
                e.stopImmediatePropagation();
                e.preventDefault();
                var subsidiaryId = $(this).val();
                var days = $("#dateRange").val();
                var runner = $("#runner").val();
                fire_ajax_submit('${dashboardPath}?subsidiaryId='+subsidiaryId+'&days='+days +'&runner='+runner);
            });
    $(document).on("change", "#dateRange", function(e){
                e.stopImmediatePropagation();
                e.preventDefault();
                var days = $(this).val();
                var subsidiaryId = $("#subsidiaryId").val();
                var runner = $("#runner").val();
                fire_ajax_submit('${dashboardPath}?subsidiaryId='+subsidiaryId+'&days='+days +'&runner='+runner);
            });
    $(document).on("change", "#runner", function(e){
                e.stopImmediatePropagation();
                e.preventDefault();
                var runner = $(this).val();
                var days = $("#dateRange").val();
                var subsidiaryId = $("#subsidiaryId").val();
                fire_ajax_submit('${dashboardPath}?subsidiaryId='+subsidiaryId+'&days='+days +'&runner='+runner);
            });

   var issueYValues = [];
   var testcaseYValues = [];
   var skuYValues = [];

   <c:forEach items="${qaSummaryData.issuesChartMap}" var="issueMap" varStatus="loop">
       var data = JSON.stringify('${issueMap}');
       issueYValues.push(data.split('=')[1].replace('"',''));
   </c:forEach>

   <c:forEach items="${qaSummaryData.skusMap}" var="skuMap" varStatus="loop">
          var data = JSON.stringify('${skuMap}');
          skuYValues.push(data.split('=')[1].replace('"',''));
      </c:forEach>

  <c:forEach items="${qaSummaryData.testCasesMap}" var="testCaseMap" varStatus="loop">
         var data = JSON.stringify('${testCaseMap}');
         testcaseYValues.push(data.split('=')[1].replace('"',''));
     </c:forEach>
   var barColors = [
      "#32b466",
      "#e62525",
      "#ff9900",
      "#12e29f",
      "#2181a1",
      "#e5fe84",
      "#720f20",
      "#f1bd02",
      "#7ffd4f",
        "#84840f",
        "#267db6"
   ];


   new Chart("myChart", {
     type: "doughnut",
     data: {
       //labels: xValues,
       datasets: [{
         backgroundColor: barColors,
         data: testcaseYValues
       }]
     },
     options: {
     maintainAspectRatio: false,
       title: {
         display: false,
         text: "Test Cases"
       }
     }
   });
   new Chart("myChart2", {
     type: "doughnut",
     data: {
       //labels: xValues,
       datasets: [{
         backgroundColor: barColors,
         data: skuYValues
       }]
     },
     options: {
       maintainAspectRatio: false,
       title: {
         display: false,
         text: "Total Skus"
       }
     }
   });
   new Chart("myChart3", {
     type: "doughnut",
     data: {
       //labels: xValues,
       datasets: [{
         backgroundColor: barColors,
         data: issueYValues
       }]
     },
     options: {
     responsive: true,
       maintainAspectRatio: false,
       title: {
         display: false,
         text: "Issues"
       }
     }
   });
});

</script>


    <script>
       var labels = [];
       var dataArray = [];
       var colors = [ '#2685CB', '#4AD95A', '#FEC81B', '#FD8D14', '#CE00E6', '#4B4AD3', '#FC3026', '#B8CCE3', '#6ADC88', '#FEE45F',"#32b466","#e62525","#ff9900","#12e29f","#2181a1","#e5fe84","#720f20"  ];

    <c:forEach items="${qaSummaryData.issuesSummaryChartMap}" var="issueSummaryChartMap" varStatus="loop">
           var issueYValues = [];
           var issueXValues = [];
           var data = JSON.stringify('${issueSummaryChartMap}');
           labels.push(data.split('=')[0].replace('"',''));
           <c:forEach items="${issueSummaryChartMap.value}" var="issueSummaryChartVal">
            var data2 = JSON.stringify('${issueSummaryChartVal}');
            var key = data2.split('=')[0].replace('"','');
            var value = data2.split('=')[1].replace('"','');
            issueXValues.push(key);
            issueYValues.push(value);
            </c:forEach>
            for(i=0;i<issueXValues.length;i++){
            dataArray[i] = {
                    barThickness: 150,
                    maxBarThickness: 300,
                    label:issueXValues[i],
                    data: issueYValues,
                    backgroundColor: colors[i],
                    hoverBackgroundColor: colors[i],
                }
                }
       </c:forEach>
       console.log(dataArray);
        // Get the drawing context on the canvas
        var myContext = document.getElementById(
            "stackedChartID").getContext('2d');
        var myChart = new Chart(myContext, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: dataArray,
            },
            options: {
            maintainAspectRatio: false,
                plugins: {
                    title: {
                        display: true,
                        text: 'Issues overview'
                    },
                },
                scales: {
                    x: {
                        stacked: true,
                    },
                    y: {
                        stacked: true
                    }
                }
            }
        });
    </script>