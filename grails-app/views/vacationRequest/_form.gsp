<%@ page import="ps.gov.epsilon.workflow.enums.v1.EnumWorkflowStatus" %>
<el:hiddenField name="employee.id" value="${vacationRequest?.employee?.id}"/>
<el:hiddenField name="person.id" value="${vacationRequest?.employee?.personId}"/>
<el:hiddenField name="vacationType.id" value="${vacationRequest?.vacationType?.id}"/>
<el:hiddenField name="workflowPathHeader" value="${workflowPathHeader}"/>

<el:hiddenField name="currentBalance"
                value="${vacationRequest?.currentBalance > 0 ? vacationRequest?.currentBalance : "0"}"/>



<g:render template="/employee/wrapperForm"
          model="[employee: vacationRequest?.employee, isVacation: true, vacationRequest: vacationRequest]"/>

<el:row/>

<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "request.info.label")}">
    <lay:widgetBody>
        <br/>
        <el:formGroup>
            <el:dateField name="requestDate" size="6" class=" isRequired " isMaxDate="true"
                          label="${message(code: 'vacationRequest.requestDate.label', default: 'requestDate')}"
                          value="${vacationRequest?.requestDate ? vacationRequest?.requestDate : java.time.ZonedDateTime.now()}"/>


            <el:labelField value="${vacationRequest?.numOfDays ?: '0'}" name="numberOfDays" id="numberOfDays" size="6"
                           label="${message(code: 'vacationRequest.numOfDays.label', default: 'number of days')}"/>

        </el:formGroup>


        <el:formGroup>

            <el:dateField name="fromDate" size="6" class=" isRequired" setMinDateFor="toDate"
                          onchange="calculateNumberOfDays();"
                          label="${message(code: 'vacationRequest.fromDate.label', default: 'fromDate')}"
                          value="${vacationRequest?.fromDate}"/>

            <el:dateField name="toDate" size="6" class=" isRequired" onchange="calculateNumberOfDays();"
                          label="${message(code: 'vacationRequest.toDate.label', default: 'toDate')}"
                          value="${vacationRequest?.toDate}"/>

        </el:formGroup>



        <el:formGroup>

            <el:textArea name="requestReason" size="6" class=""
                         label="${message(code: 'vacationRequest.requestReason.label', default: 'requestReason')}"
                         value="${vacationRequest?.requestReason}"/>

            <el:checkboxField name="external" size="6" class=" " onchange="vacationTransferValueSettings(this);"
                              label="${message(code: 'vacationRequest.external.label', default: 'external')}"
                              value="${vacationRequest?.external}" isChecked="${vacationRequest?.external}"/>
        </el:formGroup>
    </lay:widgetBody>
</lay:widget>

<el:row/>


<el:row/>
<g:if test="${params.action != "edit" && params.action != "show"}">
    <lay:widget transparent="true" color="blue" icon="icon-info-4"
                title="${g.message(code: "bordersSecurityCoordination.addressesAndPhone.label")}">
        <lay:widgetBody>
            <div class="alert alert-block alert-info  ">
                ${message(code: 'vacationRequest.alertMessage.message', default: '')}</div>


            <g:render template="/pcore/person/contactInfo/form"
                      model="${[params: [isDisabled: true, isHiddenPersonInfo: "true"], isRelatedObjectTypeDisabled: true, hideDetails: true, withPhoneNumber: true]}"/>
        </lay:widgetBody>
    </lay:widget>
</g:if>

<g:if test="${vacationRequest?.external == true}">

    <el:formGroup id="securityCoordination">

        <div class="col-md-12">
            <table width="100%">
                <tr>
                    <td style="width: 100%;">
                        <h4 class=" smaller lighter blue">${g.message(code: "vacationRequest.securityCoordinationInfo.label")}</h4>
                    </td>
                    <td width="160px" align="left">
                        %{--add edit button to select new  security coordination --}%
                        <el:modalLink
                                link="${createLink(controller: 'vacationRequest', action: 'selectedEmployeeBorders')}/${vacationRequest?.employee?.id}"
                                preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                                label="${message(code: 'vacationRequest.addBordersSecurityCoordination.label')}">
                        </el:modalLink>
                    </td>
                </tr>
            </table>
            <hr style="margin-top: 0px;margin-bottom: 9px;"/>
        </div>




    %{--represent security coordination in formal way--}%
        <lay:table styleNumber="1" id="securityCoordinationTable">
            <lay:tableHead title="${message(code: 'bordersSecurityCoordination.legalIdentifierId.label')}"/>
            <lay:tableHead title="${message(code: 'bordersSecurityCoordination.borderLocationId.label')}"/>
            <lay:tableHead title="${message(code: 'bordersSecurityCoordination.fromDate.label')}"/>
            <lay:tableHead title="${message(code: 'bordersSecurityCoordination.toDate.label')}"/>
            <rowElement>

                <g:if test="${vacationRequest?.securityCoordination}">
                    <tr class='center' id='row-0'>
                        <td class='center'>
                            <el:hiddenField name="securityCoordination.id"
                                            value="${vacationRequest?.securityCoordination?.id}"/>
                            ${vacationRequest?.securityCoordination?.transientData?.documentTypeDTO?.descriptionInfo?.localName}</td>
                        <td class='center'>${vacationRequest?.securityCoordination?.transientData?.borderCrossingPointDTO?.descriptionInfo?.localName}</td>
                        <td class='center'>${vacationRequest?.securityCoordination?.fromDate?.dateTime?.date}</td>
                        <td class='center'>${vacationRequest?.securityCoordination?.toDate?.dateTime?.date}</td>
                    </tr>
                </g:if>

            </rowElement>
        </lay:table>
    </el:formGroup>

    <script>
        $('#internalDiv').hide();
    </script>
</g:if>


<g:else>
    <el:formGroup id="securityCoordination">
        <div class="col-md-12">
            <table width="100%">
                <tr>
                    <td style="width: 100%;">
                        <h4 class=" smaller lighter blue">${g.message(code: "vacationRequest.securityCoordinationInfo.label")}</h4>
                    </td>
                    <td width="160px" align="left">
                        %{--add edit button to select new  security coordination --}%
                        <el:modalLink
                                link="${createLink(controller: 'vacationRequest', action: 'selectedEmployeeBorders')}/${vacationRequest?.employee?.id}"
                                preventCloseOutSide="true" class=" btn btn-sm btn-primary"
                                label="${message(code: 'vacationRequest.addBordersSecurityCoordination.label')}">
                        </el:modalLink>
                    </td>
                </tr>
            </table>
            <hr style="margin-top: 0px;margin-bottom: 9px;"/>
        </div>
        <lay:table styleNumber="1" id="securityCoordinationTable">
            <lay:tableHead title="${message(code: 'bordersSecurityCoordination.legalIdentifierId.label')}"/>
            <lay:tableHead title="${message(code: 'bordersSecurityCoordination.borderLocationId.label')}"/>
            <lay:tableHead title="${message(code: 'bordersSecurityCoordination.fromDate.label')}"/>
            <lay:tableHead title="${message(code: 'bordersSecurityCoordination.toDate.label')}"/>
            <rowElement><tr class='center' id='row-row-1'>
                <td class='center'>${message(code: 'vacationRequest.table.empty.message')}</td>
                <td class='center'></td>
                <td class='center'></td>
                <td class='center'></td>
            </tr>
            </rowElement>

        </lay:table>
    </el:formGroup>
</g:else>


<g:if test="${!hideManagerialOrderInfo}">
    <lay:widget transparent="true" color="green3" icon="fa fa-certificate" title="${g.message(code: "request.managerialOrderInfo.label")}">
        <lay:widgetBody>
            <g:if test="${vacationRequest?.vacationType?.needsExternalApproval}">
                <g:render template="/request/wrapperManagerialOrder" model="[request: vacationRequest, formName:'vacationRequestForm']"/>
            </g:if>
            <g:else>
                <g:render template="/request/wrapperManagerialOrder" model="[request: vacationRequest, hideExternalOrderInfo:true, formName:'vacationRequestForm']"/>
            </g:else>
        </lay:widgetBody>
    </lay:widget>
</g:if>


<g:if test="${workflowPathHeader}">
    <g:render template="/workflowPathDetails/form" model="[workflowPathHeader: workflowPathHeader]"/>
</g:if>


<g:render template="/vacationRequest/scripts"/>