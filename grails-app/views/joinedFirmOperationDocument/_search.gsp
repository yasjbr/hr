
%{--<el:formGroup>--}%
    %{--<el:autocomplete optionKey="id" optionValue="name" size="8" class="" controller="firmDocument" action="autocomplete" name="firmDocument.id" label="${message(code:'joinedFirmOperationDocument.firmDocument.label',default:'firmDocument')}" />--}%
%{--</el:formGroup>--}%
%{--<el:formGroup>--}%
    %{--<el:checkboxField name="isMandatory" size="8"  class="" label="${message(code:'joinedFirmOperationDocument.isMandatory.label',default:'isMandatory')}" />--}%
%{--</el:formGroup>--}%
<el:formGroup>
    <el:select valueMessagePrefix="EnumOperation" from="${ps.gov.epsilon.hr.enums.v1.EnumOperation.values()}" name="operation" size="8"  class="" label="${message(code:'joinedFirmOperationDocument.operation.label',default:'operation')}" />
</el:formGroup>
<el:formGroup>
    <el:integerField name="documentCount" size="8"  class="" label="${message(code:'joinedFirmOperationDocument.count.label',default:'documentCount')}" />
</el:formGroup>

