<%@ include file="../include.jsp" %>
<div class="main-content">
    <div class="row">
        <div class="col-sm-12">
            <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
        </div>
    </div>
    <c:if test="${not empty message}">
              <div class="alert alert-danger" role="alert" id="errorMessage">
                  <spring:message code="${message}" />
              </div>
          </c:if>
    <form:form method="POST" id="editForm" enctype="multipart/form-data" action="/admin/scheduler/edit" class="handle-upload" modelAttribute="editForm" >
        <div class="row">
            <div class="col-sm-12">
                <div class="dt-buttons">
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="javascript:fire_ajax_submit('/admin/scheduler')" aria-controls="tableData" title="Cancel" type="button">Cancel</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2" onclick="submitFormById('#editForm');" aria-controls="tableData" type="button" title="Save">Save</button>
                    <button class="btn btn-primary btn-icon btn-icon-small" tabindex="2"  aria-controls="tableData" type="button" id="runBtn" title="Run">Run</button>

                </div>
            </div>
        </div>
        <%@ include file="../commonFields.jsp" %>
        <div class="row">
            <div class="col-sm-3">
                <form:input path="recordId" placeholder="Record Id" style="display:none" type="hidden"/>
                <select class="cheil-select" name="cronId" id="cronId" placeholder="Select Scheduler ID" >
                <span>Select Scheduler ID</span>
                <option value="" >Select Interface</option>
                   <c:forEach items="${nodes}" var="child">
                      <c:choose>
                          <c:when test="${fn:contains( editForm.cronId, child.name ) }">
                            <option value="${child.id}" selected>${child.identifier}</option>
                          </c:when>
                          <c:otherwise>
                             <option value="${child.id}" >${child.identifier}</option>
                          </c:otherwise>
                      </c:choose>
                   </c:forEach>
                </select>

            </div>
            <div class="col-sm-3">
                <select class="cheil-select" name="mapping" id="mapping" placeholder="Select Mapping" >
                <span>Select Mapping</span>
                <option value="" >Select Mapping</option>
                   <c:forEach items="${mappings}" var="child">
                      <c:choose>
                          <c:when test="${fn:contains( editForm.mapping, child.sourceTargetId ) }">
                            <option value="${child.sourceTargetId}" selected>${child.shortDescription}</option>
                          </c:when>
                          <c:otherwise>
                             <option value="${child.sourceTargetId}" >${child.shortDescription}</option>
                          </c:otherwise>
                      </c:choose>
                   </c:forEach>
                </select>
            </div>
            <div class="col-sm-3">
                <select class="cheil-select" name="subsidiary" id="subsidiary" placeholder="Select Subsidiary" >
                <span>Select Subsidiary</span>
                <option value="" >Select Subsidiary</option>
                   <c:forEach items="${subsidiaries}" var="child">
                      <c:choose>
                          <c:when test="${editForm.subsidiary == child.id}">
                            <option value="${child.id}" selected>${child.identifier}</option>
                          </c:when>
                          <c:otherwise>
                             <option value="${child.id}" >${child.identifier}</option>
                          </c:otherwise>
                      </c:choose>
                   </c:forEach>
                </select>
            </div>
            <div class="col-sm-3" id="siteDiv">
                 <select class="cheil-select" name="sites[]" placeholder="Select Sites" multiple id="selectpicker">
                    <c:forEach items="${sites}" var="child">
                      <c:choose>
                          <c:when test="${fn:contains( selectedSites, child.siteId ) }">
                            <option value="${child.siteId}" selected>${child.siteId}</option>
                          </c:when>
                          <c:otherwise>
                             <option value="${child.siteId}" >${child.siteId}</option>
                          </c:otherwise>
                      </c:choose>
                   </c:forEach>
                 </select>
            </div>

        </div>
        <br/>
                               <br/>
        <div class="row">
            <div class="col-sm-8 col-sm-full">
                 <form:textarea path="emails" rows="5" class="inputbox-cheil-textarea-custom" placeholder="Input Recipients Eg:abc@company.com" />
                 <span>Input Recipients</span>
                 <form:errors path="emails" class="text-danger"></form:errors>
             </div>
             <div class="col-sm-4" style="margin: auto; padding: 10px;">
                 <c:choose>
                      <c:when test="${editForm.enableHistory}">
                          <c:set var="varChecked" value="'checked'"></c:set>
                      </c:when>
                      <c:otherwise>
                          <c:set var="varChecked" value=""></c:set>
                      </c:otherwise>
                  </c:choose>
                  <form:label path="enableHistory"><strong>Save History?</strong></form:label>
                  <form:checkbox path="enableHistory" checked="${varChecked}" />
             </div>
        </div>

        <br/>
        <div class="row">
             <div class="col-sm-4" id="voucher" style="display:none;">
                  <form:input id="voucherBox" path="voucherCode" class="inputbox-cheil" placeholder="Enter Voucher Code" />
                  <span>Voucher Code</span>
              </div>
        </div>
        <br/>

        <div class="row">
             <div class="col-sm-9">
                 <form:input id="cron-output" path="cronExpression" readonly="true" class="inputbox-cheil cron-output" placeholder="Cron Expression" onkeyup="importCronExpressionFromInput('#cron-output')" />
                 <span>Cron Expression</span>
            </div>
        </div>
         <br/>
         <br/>
         <div class="row">
                     <div class="col-sm-9">
                         <form:textarea path="skus" id="skus" rows="10" cols="45" class="inputbox-cheil-textarea" placeholder="please enter SKU's"/>
                         <span class="searchtext">Please enter SKU's</span>
                      </div>

         </div>
         <br/>
         <br/>
         <div class="row">
         <div class="col-sm-12" id="shortcutDiv">
               <select name="shortcuts[]" placeholder="Select Models" multiple="multiple" id="shortcuts">
                   <c:forEach items="${models}" var="child">
                     <c:choose>
                         <c:when test="${fn:contains( selectedShortcuts, child.id ) }">
                           <option value="${child.id}" selected>${child.shortDescription}</option>
                         </c:when>
                         <c:otherwise>
                            <option value="${child.id}" >${child.shortDescription}</option>
                         </c:otherwise>
                     </c:choose>
                  </c:forEach>
               </select>
            </div>
        </div>
            <br/>
                     <br/>
                     <div id="myModalCron" class="modal" tabindex="-1">
                               <div class="modal-dialog" style="max-width:900px">
                                  <div class="modal-content">
                                     <div class="modal-header">
                                         Set-up the interval time
                                        <button type="button" class="close" data-dismiss="modal">&times;</button>
                                     </div>
                                     <div class="modal-body">
                        <div class="row cron">
                <div class="col-sm-12">
                <form id="cron">
                <div class="col-sm-12 cron-shortcut">
                Common Settings
                                    <select onmousedown="this.value='';" style="height:40px;" onchange="javascript:cronTemplate(this.value);">
                                        <option value='0' selected>Select All</option>
                                        <option value='4'>Every hour</option>
                                        <option value='6'>Every 4 hours (beginning at 0am) (except for Saturday and Sunday)</option>
                                        <option value='8'>Every 12 hours (beginning at 0am) (except for Saturday and Sunday)</option>
                                        <option value='9'>Everyday at 8 am (except for Saturday and Sunday)</option>
                                        <option value='10'>Everyday</option>
                                    </select>
                                </div>
                                </div>
                                <div class="col-sm-12 cron-shortcut">
                			<div class="col-sm-6">
                				<strong>Hours:</strong><br>
                				<select size="12" multiple="multiple" name="cron-hours" class="cron-hours" id="cron-hours" onchange="updateField('hours')">
                					<option selected="selected" value="0">00</option>
                					<option selected="selected" value="1">01</option>
                					<option selected="selected" value="2">02</option>
                					<option selected="selected" value="3">03</option>
                					<option selected="selected" value="4">04</option>
                					<option selected="selected" value="5">05</option>
                					<option selected="selected" value="6">06</option>
                					<option selected="selected" value="7">07</option>
                					<option selected="selected" value="8">08</option>
                					<option selected="selected" value="9">09</option>
                					<option selected="selected" value="10">10</option>
                					<option selected="selected" value="11">11</option>
                					<option selected="selected" value="12">12</option>
                					<option selected="selected" value="13">13</option>
                					<option selected="selected" value="14">14</option>
                					<option selected="selected" value="15">15</option>
                					<option selected="selected" value="16">16</option>
                					<option selected="selected" value="17">17</option>
                					<option selected="selected" value="18">18</option>
                					<option selected="selected" value="19">19</option>
                					<option selected="selected" value="20">20</option>
                					<option selected="selected" value="21">21</option>
                					<option selected="selected" value="22">22</option>
                					<option selected="selected" value="23">23</option>
                				</select>
                				<a href="javascript:cronHelperSelectAll('#cron-hours')" style="color:blue;">Select All</a>
                			</div>
                			<div class="col-sm-6">
                				<strong>Minutes:</strong><br>
                				<select size="12" multiple="multiple" name="cron-minutes" class="cron-minutes" id="cron-minutes" onchange="updateField('minutes')">
                					<option selected="selected" value="0">00</option>
                					<option selected="selected" value="1">01</option>
                					<option selected="selected" value="2">02</option>
                					<option selected="selected" value="3">03</option>
                					<option selected="selected" value="4">04</option>
                					<option selected="selected" value="5">05</option>
                					<option selected="selected" value="6">06</option>
                					<option selected="selected" value="7">07</option>
                					<option selected="selected" value="8">08</option>
                					<option selected="selected" value="9">09</option>
                					<option selected="selected" value="10">10</option>
                					<option selected="selected" value="11">11</option>
                					<option selected="selected" value="12">12</option>
                					<option selected="selected" value="13">13</option>
                					<option selected="selected" value="14">14</option>
                					<option selected="selected" value="15">15</option>
                					<option selected="selected" value="16">16</option>
                					<option selected="selected" value="17">17</option>
                					<option selected="selected" value="18">18</option>
                					<option selected="selected" value="19">19</option>
                					<option selected="selected" value="20">20</option>
                					<option selected="selected" value="21">21</option>
                					<option selected="selected" value="22">22</option>
                					<option selected="selected" value="23">23</option>
                					<option selected="selected" value="24">24</option>
                					<option selected="selected" value="25">25</option>
                					<option selected="selected" value="26">26</option>
                					<option selected="selected" value="27">27</option>
                					<option selected="selected" value="28">28</option>
                					<option selected="selected" value="29">29</option>
                					<option selected="selected" value="30">30</option>
                					<option selected="selected" value="31">31</option>
                					<option selected="selected" value="32">32</option>
                					<option selected="selected" value="33">33</option>
                					<option selected="selected" value="34">34</option>
                					<option selected="selected" value="35">35</option>
                					<option selected="selected" value="36">36</option>
                					<option selected="selected" value="37">37</option>
                					<option selected="selected" value="38">38</option>
                					<option selected="selected" value="39">39</option>
                					<option selected="selected" value="40">40</option>
                					<option selected="selected" value="41">41</option>
                					<option selected="selected" value="42">42</option>
                					<option selected="selected" value="43">43</option>
                					<option selected="selected" value="44">44</option>
                					<option selected="selected" value="45">45</option>
                					<option selected="selected" value="46">46</option>
                					<option selected="selected" value="47">47</option>
                					<option selected="selected" value="48">48</option>
                					<option selected="selected" value="49">49</option>
                					<option selected="selected" value="50">50</option>
                					<option selected="selected" value="51">51</option>
                					<option selected="selected" value="52">52</option>
                					<option selected="selected" value="53">53</option>
                					<option selected="selected" value="54">54</option>
                					<option selected="selected" value="55">55</option>
                					<option selected="selected" value="56">56</option>
                					<option selected="selected" value="57">57</option>
                					<option selected="selected" value="58">58</option>
                					<option selected="selected" value="59">59</option>
                				</select>
                				<a href="javascript:cronHelperSelectAll('#cron-minutes')" style="color:blue;">Select All</a>
                			</div>
                			</div>
                			<div class="col-sm-12 cron-shortcut">
                			<div class="col-sm-6">
                				<strong>Day of Month:</strong><br>
                				<select size="12" multiple="multiple" name="cron-dom" id="cron-dom" class="cron-dom" onchange="updateField('dom')">
                					<option selected="selected" value="1">01</option>
                					<option selected="selected" value="2">02</option>
                					<option selected="selected" value="3">03</option>
                					<option selected="selected" value="4">04</option>
                					<option selected="selected" value="5">05</option>
                					<option selected="selected" value="6">06</option>
                					<option selected="selected" value="7">07</option>
                					<option selected="selected" value="8">08</option>
                					<option selected="selected" value="9">09</option>
                					<option selected="selected" value="10">10</option>
                					<option selected="selected" value="11">11</option>
                					<option selected="selected" value="12">12</option>
                					<option selected="selected" value="13">13</option>
                					<option selected="selected" value="14">14</option>
                					<option selected="selected" value="15">15</option>
                					<option selected="selected" value="16">16</option>
                					<option selected="selected" value="17">17</option>
                					<option selected="selected" value="18">18</option>
                					<option selected="selected" value="19">19</option>
                					<option selected="selected" value="20">20</option>
                					<option selected="selected" value="21">21</option>
                					<option selected="selected" value="22">22</option>
                					<option selected="selected" value="23">23</option>
                					<option selected="selected" value="24">24</option>
                					<option selected="selected" value="25">25</option>
                					<option selected="selected" value="26">26</option>
                					<option selected="selected" value="27">27</option>
                					<option selected="selected" value="28">28</option>
                					<option selected="selected" value="29">29</option>
                					<option selected="selected" value="30">30</option>
                					<option selected="selected" value="31">31</option>
                				</select>
                				<a href="javascript:cronHelperSelectAll('#cron-dom')" style="color:blue;">Select All</a>
                			</div>
                			<div class="col-sm-6">
                				<strong>Month:</strong><br>
                				<select size="12" multiple="multiple" name="cron-months" id="cron-months" class="cron-months" onchange="updateField('months')">
                					<option selected="selected" value="1">January</option>
                					<option selected="selected" value="2">February</option>
                					<option selected="selected" value="3">March</option>
                					<option selected="selected" value="4">April</option>
                					<option selected="selected" value="5">May</option>
                					<option selected="selected" value="6">June</option>
                					<option selected="selected" value="7">July</option>
                					<option selected="selected" value="8">August</option>
                					<option selected="selected" value="9">September</option>
                					<option selected="selected" value="10">October</option>
                					<option selected="selected" value="11">November</option>
                					<option selected="selected" value="12">December</option>
                				</select>
                				<a href="javascript:cronHelperSelectAll('#cron-months')" style="color:blue;">Select All</a>
                			</div>
                			</div>
                			<div class="col-sm-12 cron-shortcut">
                			<div class="col-sm-6">
                				<strong>Day of Week:</strong><br>
                				<select size="12" multiple="multiple" name="cron-dow" id="cron-dow" class="cron-dow" onchange="updateField('dow')">
                					<option selected="selected" value="0">Sunday</option>
                					<option selected="selected" value="1">Monday</option>
                					<option selected="selected" value="2">Tuesday</option>
                					<option selected="selected" value="3">Wednesday</option>
                					<option selected="selected" value="4">Thursday</option>
                					<option selected="selected" value="5">Friday</option>
                					<option selected="selected" value="6">Saturday</option>
                				</select>
                				<a href="javascript:cronHelperSelectAll('#cron-dow')" style="color:blue;">Select All</a>
                			</div>
                			</div>
                			<span style="display:block;clear:both">&nbsp;</span>
                			<div class="col-sm-12" style="text-align:center;">
                			    <button id="closePopupSave" class="btn btn-primary btn-icon" type="button" title="Save">Save</button>
                			</div>
                		</form>
</div>
              </div>
           </div>
                </div>

                </div>
            </div>
       </form:form>
       </div>

    </div>
  </div>
</div>

<script type="text/javascript">
    var sitesData = '';
   $(document).ready(function() {
   $("#cron-output").click(function(){
         $("#myModalCron").modal('show');
        });
        $("#closePopupSave").click(function(){
         $("#myModalCron").modal('hide');
        });

   $('#shortcuts').multiselect({
                     enableFiltering: true,
                     includeSelectAllOption: true,
                     buttonWidth: '100%',
                     resetText: "Reset all",
                     includeResetOption: true,
                     includeResetDivider: true,
                     maxHeight: 400,
                     dropUp: true
    });
        sitesData = new Choices('#selectpicker', {
                       removeItemButton: true,
                       maxItemCount:30,
                       searchResultLimit:30,
                       renderChoiceLimit:-1
                     });
        var multipleCancelButton = new Choices('#schedulerId', {
                                          removeItemButton: true,
                                          maxItemCount:-1,
                                          searchResultLimit:20,
                                          renderChoiceLimit:-1
                                        });
        var multipleCancelButton2 = new Choices('#mapping', {
                                          removeItemButton: true,
                                          maxItemCount:-1,
                                          searchResultLimit:20,
                                          renderChoiceLimit:-1
                                        });
       var multipleCancelButton3 = new Choices('#subsidiary', {
                                         removeItemButton: true,
                                         maxItemCount:-1,
                                         searchResultLimit:20,
                                         renderChoiceLimit:-1
                                       });
      /*$('#cronDiv').cronPicker({
              // time format, either 12 hours or 24 hours (default)
              format: '24',

              // available formatters:
              //   - StandardCronFormatter (crontab specification)
              //   - QuartzCronFormatter (quartz.net specification)
              cronFormatter: QuartzCronFormatter,

              // callback function called each time cron expression is updated
              onCronChanged: function (cron) {
                $('#cronExpression').val(cron);
                  console.log(cron);
              }
          });*/

  });
  $("#subsidiary").change(function(){
            var categoryId = $(this).val();
            $.ajax({
                type: 'GET',
                url: "/toolkit/import/getSitesForSubsidiary/" + categoryId,
                datatype: "json",
                success: function(data){
                $('#siteDiv').show();
                var jsonData = [];
                  for(var i=0; i<data.length; i++){
                      jsonData.push({
                              "value" : data[i].siteId,
                              "label" : data[i].siteId
                          });
                  }
                  sitesData.clearStore();
                  sitesData.setChoices(jsonData,'value','label', true);
                },
                error:function(e){
                    console.log(e.statusText);
                }
            });
        });
         $("#subsidiary").change(function(){
         $('#shortcuts').multiselect('destroy');
                  var categoryId = $(this).val();
                  $.ajax({
                      type: 'GET',
                      url: "/toolkit/import/getModelForSubsidiary/" + categoryId,
                      datatype: "json",
                      success: function(data){
                      //$('#shortcutDiv').show();
                       var slctSubcat=$('#shortcuts'), option="";
                            slctSubcat.empty();
                              for(var i=0; i<data.length; i++){
                                  option = option + "<option value='"+data[i].id + "'>"+data[i].shortDescription.replaceAll("?", "") + "</option>";
                              }
                          slctSubcat.append(option);
                          //$('#shortcuts').multiselect('rebuild');
                          $('#shortcuts').multiselect({
                                          enableFiltering: true,
                                          includeSelectAllOption: true,
                                          buttonWidth: '100%',
                                          resetText: "Reset all",
                                          includeResetOption: true,
                                          includeResetDivider: true,
                                          maxHeight: 400,
                                          dropUp: true
                         });

                        },
                      error:function(e){
                          console.log(e.statusText);
                      }
                  });
              });
              $("#mapping").change(function(){
              $('#voucher').hide();
                var categoryId = $(this).val();
                $.ajax({
                    type: 'GET',
                    url: "/admin/scheduler/isVoucherEnabled/" + categoryId,
                    datatype: "json",
                    success: function(data){
                    if(data==true){
                        $('#voucher').show();
                    }
                    else{
                        $('#voucherBox').val("");
                    }
                    },
                    error:function(e){
                        console.log(e.statusText);
                    }
                });
            });
                  function removeRow(countVal) {
                                $('#scheduler'+countVal).remove();
                             }
                              $("#runBtn").click(function(){
                                   var form = $('#editForm');
                                        $.ajax({
                                            type: 'POST',
                                            data: form.serialize(),
                                            url: "/admin/scheduler/run",
                                            datatype: "json",
                                            success: function(data){
                                            if(data == 'Success'){
                                                fire_ajax_submit('${contextPath}/admin/scheduler');
                                            }

                                            },
                                            error:function(e){
                                                console.log(e.statusText);
                                            }
                                        });
                                    });

</script>