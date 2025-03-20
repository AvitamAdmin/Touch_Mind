<div class="main-content">
  <div class="row">
    <div class="col-sm-12">
      <form method="POST" id="operationForm" enctype="multipart/form-data" action="/handleOperation" class="handle-upload" modelAttribute="uploadForm" >
        <div class="row">
              <div class="col-xs-9">
                <label class="button" for="apiUrl">Please input SKU codes</label><br/>
                <input type="hidden" id="checkType" name="checkType" value="">
                <textarea id="skus" name="skus" rows="13" cols="70"></textarea>
              </div>
              <div class="col-xs-3">
                <br/>
                <input type="submit" onclick="handleOperationForm(this);" value="Check Stock"/>
                <input type="submit" onclick="handleOperationForm(this);" value="Check Price" />
                <input type="submit" onclick="submitOperationForm(this);" value="Check AddOn" />
                <input type="submit" onclick="submitOperationForm(this);" value="Check TradeIn" />
                <input type="submit" onclick="submitOperationForm(this);" value="Check SIM" />
                <input type="submit" onclick="submitOperationForm(this);" value="Check eWarranty" />
                <input type="submit" onclick="submitOperationForm(this);" value="Check EUP1" />
              </div>
        </div>
        <div class="row">
            <div class="col-xs-5">
                For your convenience use the shortcut<br/>
                <select class="selectpicker cheil-select" multiple size="11"  name="checkOptions" id="checkOptions" data-live-search="true">
                    <option value="CheckS22">Check S22|S22+</option>
                    <option value="CheckS22Ultra">Check S22Ultra</option>
                    <option value="CheckTabS8">Check TabS8</option>
                    <option value="CheckZflip3">Check Zflip3</option>
                    <option value="CheckA53">Check A53</option>
                    <option value="CheckZfold3">Check Zfold3</option>
                    <option value="CheckBuds">Check Buds</option>
                    <option value="CheckA52">Check A52</option>
                    <option value="CheckTabA8">Check TabA8</option>
                    <option value="CheckWatch4">Check Watch4</option>
                    <option value="GalaxyZFold4SiliconeGripCover">Galaxy Z Fold4 Silicone Grip Cover</option>
                    <option value="GalaxyZFold4NotePackage"> Galaxy Z Fold4 Note Package</option>
                    <option value="GalaxyZFold4LeatherCover">Galaxy Z Fold4 Leather Cover</option>
                    <option value="GalaxyZFlip4SiliconeCoverwithStrap">Galaxy Z Flip4 Silicone Cover with Strap</option>
                    <option value="GalaxyZFlip4BespokeEdition(YellowFrontColor)">Galaxy Z Flip4 Bespoke Edition (Yellow Front Color)</option>
                    <option value="GalaxyZFlip4FlapLeatherCover">Galaxy Z Flip4 Flap Leather Cover</option>
                    <option value="GalaxyWatch5ProBluetooth(45mm)">Galaxy Watch5 Pro Bluetooth (45mm)</option>
                    <option value="GalaxyZFlip4ClearCoverwithRing">Galaxy Z Flip4 Clear Cover with Ring</option>
                    <option value="GalaxyWatch5Bluetooth40mmSM-R900">Galaxy Watch5 Bluetooth 40mm SM-R900</option>
                    <option value="GalaxyZFlip4">Galaxy Z Flip4</option>
                    <option value="GalaxyWatch5ProLTE45mmSM-R925">Galaxy Watch5 Pro LTE 45mm SM-R925</option>
                    <option value="GalaxyWatch540mmMilaneseBand"> GalaxyWatch5 40mm Milanese Band</option>
                    <option value="GalaxyZFold4">Galaxy Z Fold4</option>
                    <option value="GalaxyZFold4StandingCoverwithPen"> Galaxy Z Fold4 Standing Cover with Pen</option>
                    <option value="GalaxyZFold4FrontProtectionFilm"> Galaxy Z Fold4 Front Protection Film</option>
                    <option value="GalaxyWatch5Bluetooth(44mm)">Galaxy Watch5 Bluetooth (44mm)</option>
                    <option value="GalaxyWatch5LTE(44mm)">Galaxy Watch5 LTE (44mm)</option>
                    <option value="GalaxyZFlip4ClearSlimCover">Galaxy Z Flip4 Clear Slim Cover</option>
                    <option value="GalaxyWatch5LTE(40mm)">Galaxy Watch5 LTE (40mm)</option>
                    <option value="GalaxyZFlip4SiliconeCoverwithRing">Galaxy Z Flip4 Silicone Cover with Ring</option>
                    <option value="GalaxyWatch544mmMilaneseBand"> GalaxyWatch5 44mm Milanese Band</option>
                    <option value="GalaxyZFold4SlimStandingCover">Galaxy Z Fold4 Slim Standing Cover</option>
                    <option value="GalaxyZFlip4BespokeEdition">Galaxy Z Flip4 Bespoke Edition</option>
                </select>
              </div>
              <div class="col-xs-7">
                <label for"site">For check price select partner site </label><br/>
                <select class="selectpicker cheil-select" multiple size="11"  name="sites" id="site" data-live-search="true">
                   <option value="de" selected>de</option>
                   <option value="allianz">allianz</option>
                   <option value="corporatebenefits">corporatebenefits</option>
                   <option value="de_test">de_test</option>
                   <option value="deebay">deebay</option>
                   <option value="desme">desme</option>
                   <option value="deupgradelounge">deupgradelounge</option>
                   <option value="dfb">dfb</option>
                   <option value="drk">drk</option>
                   <option value="ebt_demo">ebt_demo</option>
                   <option value="ergo">ergo</option>
                   <option value="geno_gruppe">geno_gruppe</option>
                   <option value="harmankardon">harmankardon</option>
                   <option value="iamstudent">iamstudent</option>
                   <option value="insiders">insiders</option>
                   <option value="isic">isic</option>
                   <option value="paritaeter">paritaeter</option>
                   <option value="sparkasse">sparkasse</option>
                   <option value="studentenrabatt">studentenrabatt</option>
                   <option value="uk_upgradelounge">uk_upgradelounge</option>
                   <option value="unidays">unidays</option>
                   <option value="vorteilsprogramm">vorteilsprogramm</option>
                   <option value="wgkd">wgkd</option>
               </select>
              </div>

        </div>
            <c:if test="${not empty message}">
             <div class="row">
                <div class="col-xs-12">
                    <div class="alert alert-danger" role="alert">
                        <spring:message code="${message}" />
                    </div>
                </div>
                </div>
            </c:if>
      </form>
    </div>
  </div>
</div>