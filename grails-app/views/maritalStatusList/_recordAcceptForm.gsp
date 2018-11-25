<% def size= colSize?:6 %>

<g:if test="${size == 6}">
    <el:formGroup>
        <el:checkboxField label="${message(code: 'childList.hasAllowance.label', default: 'hasAllowance')}"
                          size="${size}"
                          id="hasAllowance"
                          name="hasAllowance"/>

        <div id="effectiveDateDiv" style="display: none">
            <el:dateField name="effectiveDate" size="${size}" class=" isRequired"
                          label="${message(code: 'allowanceRequest.effectiveDate.label', default: 'effectiveDate')}"
                          value=""/>
        </div>
    </el:formGroup>
</g:if>
<g:else>
    <el:formGroup>
        <el:checkboxField label="${message(code: 'childList.hasAllowance.label', default: 'hasAllowance')}"
                          size="${size}"
                          id="hasAllowance"
                          name="hasAllowance"/>

    </el:formGroup>
    <el:formGroup>
        <div id="effectiveDateDiv" style="display: none">
            <el:dateField name="effectiveDate" size="${size}" class=" isRequired"
                          label="${message(code: 'allowanceRequest.effectiveDate.label', default: 'effectiveDate')}"
                          value=""/>
        </div>
    </el:formGroup>
</g:else>
<script type="text/javascript">

    $('#hasAllowance_').change(function() {
        if (this.checked) {
            $('#effectiveDateDiv').show(500);
        } else {
            $('#effectiveDateDiv').hide(100);
        }
    });
</script>