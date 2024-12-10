<%@ include file="../include.jsp" %>
<div class="main-content">
   <div class="row">
      <div class="col-sm-5">
         <p class="navigation" id="navBreadcrumb">Breadcrumb</p>
      </div>
      <div class="col-sm-7"></div>
   </div>
   <form:form method="POST" id="editForm" enctype="multipart/form-data" action="${path}" class="handle-upload" modelAttribute="editForm" >
   <div class="row" id="cheil-row">
      <div class="col-sm-5">
         <div class="dt-buttons">
            <button class="btn btn-primary btn-icon btn-icon-small" title="Upload" id="genUploadBtn" tabindex="0" aria-controls="tableData" type="button">Upload</button>
            <button class="btn btn-primary btn-icon btn-icon-small" title="Submit" onclick="ajaxformSubmit('editForm');" tabindex="0" aria-controls="tableData" type="submit">Submit</button>
            </div>
      </div>
      <div class="col-sm-7"></div>
   </div>
</br>
</br>

<div class="row">
           <c:forEach items="${allParams}" var="allParam">
           </br>
           </br>

           <c:forEach items="${allParam}" var="childParam">

           <c:forEach items="${paramMap}" var="child" varStatus="loop">

               <c:if test="${childParam.param == child.key}">
               <c:set var="fieldValues" value="${fn:split(child.value, '$%')}" />

                 <c:choose>
                <c:when test="${fieldValues[0]=='Site Loader'}">
                <div class="col-sm-6">
                 </c:when>
                <c:otherwise>
               <div class="col-sm-3" style="margin-bottom:40px;">
               </c:otherwise>
               </c:choose>
               <c:if test="${fieldValues[0]=='Formular' || fieldValues[0]=='Input Box'}">
                   <textarea rows="5" name="${child.key}" class="inputbox-cheil-textarea" placeholder="Input ${childParam.header}">${fieldValues[1]}</textarea>
                                      <span>Input ${childParam.header}</span>
                  </c:if>
                  <c:if test="${fieldValues[0]=='Site Loader'}">
                  <select name="${child.key}" placeholder="Select ${childParam.header}" multiple="multiple" class="${child.key}" id="selectpicker${loop.index}">
                     <c:forEach items="${subsidiaries}" var="childSub">
                     <c:set var="siteChannelMap" value="${childSub.value}"/>
                       <c:forEach items="${childSub.value}" var="siteMap">
                       <optgroup label="${childSub.key}-${siteMap.key}">
                       <c:forEach items="${siteChannelMap[siteMap.key]}" var="siteId">
                       <c:choose>
                             <c:when test="${fn:contains( editForm.sites, siteId ) && fn:contains( editForm.subsidiaries, childSub.key ) }">
                               <option value="${childSub.key}-${siteId}" selected>${siteId}</option>
                             </c:when>
                             <c:otherwise>
                                <option value="${childSub.key}-${siteId}" >${siteId}</option>
                             </c:otherwise>
                         </c:choose>
                         </c:forEach>
                        </optgroup>
                       </c:forEach>
                        </c:forEach>
                   </select>
                   </br>
                   </br>
</c:if>
               <c:if test="${fieldValues[0] == 'Dropdown'}">
                   <select class="cheil-select" name="${child.key}">
                      <option value="">Select ${childParam.header}</option>
                      <c:forEach items="${fn:split(fieldValues[1], ',')}" var="child1">
                          <c:choose>
                               <c:when test="${fn:contains( fieldValues[2], child1 ) }">
                                 <option value="${child1}" selected>${child1}</option>
                               </c:when>
                               <c:otherwise>
                                  <option value="${child1}" >${child1}</option>
                               </c:otherwise>
                           </c:choose>
                      </c:forEach>
                  </select>
              </c:if>
              <c:if test="${fieldValues[0]=='Date and Time selector'}">
              <input type="datetime-local" data-date="${fieldValues[1]}" data-date-format="${fieldValues[1]}" name="${child.key}" id="datepicker${loop.index}" class="inputbox-cheil-with-date" placeholder="Input ${childParam.header}"  autocomplete="off"/>
                                 <span class="searchtext">${childParam.header}</span>
             </c:if>

</c:if>
           </c:forEach>
           </div>

           </c:forEach>

           </c:forEach>
           </div>
</form:form>
</div>
<div id="myGeneratorUpload" class="modal fade" tabindex="-1">
      <div class="modal-dialog" style="max-width: 50%; margin-top:120px; margin-left: 350px;">
         <div class="modal-content">
            <div class="modal-header" style="border-bottom:none;">
            Upload
               <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
               <form:form method="POST" id="uploadForm" enctype="multipart/form-data" action="${path}/upload" class="handle-upload" modelAttribute="uploadForm">
                    <div class="form-group files">
                    <input class="file-input" type="file" name="file" path="file" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
                    </div>
               <div style="text-align:center;">
                   <input type="submit" value="Upload" onClick="ajaxformSubmit('uploadForm');"/>
               </div>
               </form:form>
            </div>
         </div>
      </div>
   </div>
<script type="text/javascript">
   $(document).ready(function() {
   $('[id*="selectpicker"]').multiselect({
                       enableClickableOptGroups: true,
                       enableCollapsibleOptGroups: true,
                       collapseOptGroupsByDefault:true,
                       enableFiltering: true,
                       includeSelectAllOption: true,
                       buttonWidth: '100%',
                       resetText: "Reset all",
                       includeResetOption: true,
                       includeResetDivider: true,
                       enableFiltering: true,
                       enableHTML: true,
                       dropUp: true,
                       maxHeight: 300,
                       buttonText: function(options, select) {
                               var $select = $(select);
                               var $optgroups = $('optgroup', $select);
                                       if (options.length === 0) {
                                           return 'Input ' + $(select).attr("class");
                                       }
                                        else {
                                           var text='';
                                            $optgroups.each(function() {
                                                 var $selectedOptions = $('option:selected', this);
                                                 var $options = $('option', this);
                                                 if ($selectedOptions.length == $options.length) {
                                                     text += '<span class="boot-choices">' + $(this).attr('label') + '|' + 'All Sites ' + '</span>';
                                                 }
                                                 else if($selectedOptions.length > 0) {
                                                     text += '<span class="boot-choices">' + $(this).attr('label') + '|';
                                                     $selectedOptions.each(function() {
                                                         text += $(this).text() + ',';
                                                     });
                                                     text = text.substr(0, text.length - 1);
                                                     text += '</span>';
                                                 }
                                             });
                                             return text;
                                        }
                                   }
                   });

   $("#genUploadBtn").click(function(){
          $("#myGeneratorUpload").modal('show');
      });

      $('[id*="datepicker"]').on('input',function(e){
      this.setAttribute(
              "data-date",
              moment(this.value)
              .format( this.getAttribute("data-date-format") )
      )
      }).trigger("change");

});
</script>
</div>