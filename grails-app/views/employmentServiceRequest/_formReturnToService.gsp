<el:hiddenField name="employee.id" value="${employmentServiceRequest?.employee?.id}"/>
<el:hiddenField name="requestType" value="${ps.gov.epsilon.hr.enums.v1.EnumRequestType.RETURN_TO_SERVICE}"/>
<g:render template="/employee/wrapperForm" model="[employee: employmentServiceRequest?.employee]"/>

%{--<g:if test="${employmentServiceRequest?.transientData?.endOfServiceRequest}">--}%

%{--<lay:widget transparent="true" color="blue" icon="icon-asterisk"--}%
            %{--title="${g.message(code: "endOfService.details.label")}">--}%
    %{--<lay:widgetBody>--}%
        %{--<lay:showWidget size="6">--}%
            %{--<lay:showElement--}%
                    %{--value="${employmentServiceRequest?.transientData?.endOfServiceRequest?.id}"--}%
                    %{--type="String"--}%
                    %{--label="${message(code: 'employmentServiceRequest.id.label', default: 'id')}"/>--}%
            %{--<lay:showElement--}%
                    %{--value="${employmentServiceRequest?.transientData?.endOfServiceRequest?.orderDate}"--}%
                    %{--type="ZonedDate"--}%
                    %{--label="${message(code: 'employmentServiceRequest.orderDate.label', default: 'orderDate')}"/>--}%
            %{--<lay:showElement--}%
                    %{--value="${employmentServiceRequest?.transientData?.endOfServiceRequest?.expectedDateEffective}"--}%
                    %{--type="ZonedDate"--}%
                    %{--label="${message(code: 'employmentServiceRequest.expectedDateEffective.label', default: 'expectedDateEffective')}"/>--}%

        %{--</lay:showWidget>--}%
        %{--<lay:showWidget size="6">--}%
            %{--<lay:showElement--}%
                    %{--value="${employmentServiceRequest?.transientData?.endOfServiceRequest?.requestDate}"--}%
                    %{--type="ZonedDate"--}%
                    %{--label="${message(code: 'employmentServiceRequest.requestDate.label', default: 'requestDate')}"/>--}%
            %{--<lay:showElement value="${employmentServiceRequest?.transientData?.endOfServiceRequest?.orderNo}"--}%
                             %{--type="String"--}%
                             %{--label="${message(code: 'employmentServiceRequest.orderNo.label', default: 'orderNo')}"/>--}%
            %{--<lay:showElement value="${employmentServiceRequest?.transientData?.endOfServiceRequest?.serviceActionReason}"--}%
                             %{--type="String"--}%
                             %{--label="${message(code: 'employmentServiceRequest.serviceActionReason.label', default: 'serviceActionReason')}"--}%
                             %{--/>--}%
        %{--</lay:showWidget>--}%
    %{--</lay:widgetBody>--}%
%{--</lay:widget>--}%

    %{--</g:if><g:else>--}%
    %{--<g:render template="/suspensionRequest/wrapperForm" model="[suspensionRequest: employmentServiceRequest?.transientData?.suspensionRequest]"/>--}%
%{--</g:else>--}%
<br/>
<el:row/>
<br/>

<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "request.info.label")}">
    <lay:widgetBody>
        <br/>
        <el:formGroup>
            <el:dateField name="requestDate" size="6" class=" isRequired"
                          label="${message(code: 'employmentServiceRequest.requestDate.label', default: 'requestDate')}"
                          value="${employmentServiceRequest?.requestDate}"/>
            <el:textField name="serviceActionReason"
                          isDisabled="true"
                          size="6"
                          label="${message(code: 'recallToService.serviceActionReason.label', default: 'serviceActionReason')}"
                          value="${employmentServiceRequest?.serviceActionReason?.descriptionInfo?.localName}"
                          class=""/>
            <el:hiddenField name="serviceActionReason.id" value="${employmentServiceRequest?.serviceActionReason?.id}" />
        </el:formGroup>
        <el:formGroup>
            <el:textArea name="requestStatusNote" size="6" class=""
                         label="${message(code: 'employmentServiceRequest.requestStatusNote.label', default: 'requestStatusNote')}"
                         value="${employmentServiceRequest?.requestStatusNote}"/>
        </el:formGroup>
    </lay:widgetBody>
</lay:widget>

<g:if test="${!hideManagerialOrderInfo}">
    <lay:widget transparent="true" color="green3" icon="fa fa-certificate" title="${g.message(code: "request.managerialOrderInfo.label")}">
        <lay:widgetBody>
            <g:render template="/request/wrapperManagerialOrder" model="[request:employmentServiceRequest, formName:'employmentServiceRequestForm',parentFolder:'serviceList']"/>
        </lay:widgetBody>
    </lay:widget>
</g:if>


<g:if test="${workflowPathHeader}">
    <g:render template="/workflowPathDetails/form" model="[workflowPathHeader: workflowPathHeader]"/>
</g:if>


