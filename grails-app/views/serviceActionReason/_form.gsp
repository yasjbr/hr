<g:render template="/DescriptionInfo/wrapper" model="[bean: serviceActionReason?.descriptionInfo]"/>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="serviceActionReasonType"
                     action="autocomplete" name="serviceActionReasonType.id"
                     label="${message(code: 'serviceActionReason.serviceActionReasonType.label', default: 'serviceActionReasonType')}"
                     values="${[[serviceActionReason?.serviceActionReasonType?.id, serviceActionReason?.serviceActionReasonType?.descriptionInfo?.localName]]}"/>
</el:formGroup>
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="employeeStatus"
                     action="autocomplete" name="employeeStatusResult.id"
                     label="${message(code: 'serviceActionReason.employeeStatusResult.label', default: 'employeeStatusResult')}"
                     values="${[[serviceActionReason?.employeeStatusResult?.id, serviceActionReason?.employeeStatusResult?.descriptionInfo?.localName]]}"/>
</el:formGroup>
%{--<el:formGroup>--}%
    %{--<el:checkboxField name="allowReturnToService" size="8"--}%
                      %{--class=""--}%
                      %{--label="${message(code:'serviceActionReason.allowReturnToService.label',default:'allowReturnToService')}"--}%
                      %{--value="${serviceActionReason?.allowReturnToService}"--}%
                      %{--isChecked="${serviceActionReason?.allowReturnToService}" />--}%
%{--</el:formGroup>--}%
<el:formGroup>
    <el:textField name="universalCode" size="8" class=""
                  label="${message(code: 'serviceActionReason.universalCode.label', default: 'universalCode')}"
                  value="${serviceActionReason?.universalCode}"/>
</el:formGroup>