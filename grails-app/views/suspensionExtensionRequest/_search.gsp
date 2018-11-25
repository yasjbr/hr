<el:formGroup>

    <el:textField name="id" size="6" label="${message(code: 'suspensionExtensionRequest.id.label')}"/>

    <el:range type="date" size="6" name="requestDate"
              label="${message(code: 'suspensionExtensionRequest.requestDate.label')}"/>

</el:formGroup>

<el:formGroup>

    <el:range type="date" size="6" name="fromDate"
              label="${message(code: 'suspensionExtensionRequest.fromDate.label')}"/>

    <el:range type="date" size="6" name="toDate"
              label="${message(code: 'suspensionExtensionRequest.toDate.label')}"/>

</el:formGroup>


<el:formGroup>
    <el:integerField name="periodInMonth" size="6" class=" isNumber"
                     label="${message(code: 'suspensionExtensionRequest.periodInMonth.label', default: 'periodInMonth')}"/>

</el:formGroup>


