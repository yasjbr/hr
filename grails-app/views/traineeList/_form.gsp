<div style="padding-right: 40px;,padding-bottom: 15px;">

    <h4 class=" smaller lighter blue">
        ${message(code: 'traineeList.info.label')}</h4> <hr/></div>

<el:formGroup>
    <el:textField name="name"
                  size="8"
                  class=" isRequired"
                  label="${message(code: 'traineeList.name.label', default: 'name')}"
                  value="${traineeList?.name}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="fromDate"
                  size="8"
                  class=" isRequired" setMinDateFor="toDate"
                  label="${message(code: 'traineeList.fromDate.label', default: 'fromDate')}"
                  value="${traineeList?.fromDate}"/>
</el:formGroup>

<el:formGroup>
    <el:dateField name="toDate"
                  size="8"
                  class=" isRequired" setMaxDateFor="fromDate"
                  label="${message(code: 'traineeList.toDate.label', default: 'toDate')}"
                  value="${traineeList?.toDate}"/>
</el:formGroup>





<el:formGroup>
    <el:select valueMessagePrefix="EnumReceivingParty"
               from="${ps.gov.epsilon.hr.enums.v1.EnumReceivingParty.values()}"
               name="receivingParty"
               size="8"
               class=""
               label="${message(code: 'traineeList.receivingParty.label', default: 'receivingParty')}"
               value="${traineeList?.receivingParty}"/>
</el:formGroup>

<lay:widget transparent="true" color="blue" icon="icon-location"
            title="${g.message(code: "traineeList.location.label")}">
    <lay:widgetBody>
        <br/>

        <el:hiddenField name="locationId" value="${traineeList?.trainingLocationId}"/>
        <g:render template="/pcore/location/staticWrapper"
                  model="[location          : traineeList?.transientData?.locationDTO,
                          isRequired        : false,
                          isRequiredFields  : false,
                          size              : 8,
                          isRegionRequired  : false,
                          isCountryRequired : true,
                          isDistrictRequired: false,
                          hiddenDetails     : true]"/>
        <el:formGroup>
            <el:textArea name="unstructuredLocation" size="8" class=" "
                         label="${message(code: 'traineeList.unstructuredLocation.label', default: 'unstructuredLocation')}"
                         value="${traineeList?.unstructuredLocation}"/>
        </el:formGroup>

    </lay:widgetBody>
</lay:widget>


<el:formGroup>
    <el:textAreaDescription name="coverLetter" size="8" class=" "
                            label="${message(code: 'list.coverLetter.label', default: 'coverLetter')}"
                            value="${traineeList?.coverLetter}" />

    <el:modalLink preventCloseOutSide="true" class="btn btn-sm btn-round width-135"
                  link="${createLink(controller: 'correspondenceTemplate', action: 'listModal')}"
                  label="">
        <i class="fa fa-hand-o-up"></i>
        <g:message code="default.button.select.label" />
    </el:modalLink>
</el:formGroup>

