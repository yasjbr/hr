<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "endorseOrder.info.label")}">
    <lay:widgetBody>

        <el:hiddenField name="loanNominatedEmployeeEncodedId" value="${loanNominatedEmployeeEncodedId}" />
        <msg:warning label="${message(code:'endorseOrder.endorseOrderWarning.label')}" />

        <el:formGroup>
            <el:dateField name="effectiveDate"  size="8" class=" isRequired"
                          label="${message(code:'endorseOrder.effectiveDate.label',default:'effectiveDate')}"
                          value="${endorseOrder?.effectiveDate}" />
        </el:formGroup>

        <el:formGroup>
            <el:textField name="orderNo" size="8"  class=""
                          label="${message(code:'endorseOrder.orderNo.label',default:'orderNo')}"
                          value="${endorseOrder?.orderNo}"/>
        </el:formGroup>

        <el:formGroup>
            <el:dateField name="orderDate"  size="8" class=" isRequired"
                          label="${message(code:'endorseOrder.orderDate.label',default:'orderDate')}"
                          value="${endorseOrder?.orderDate}" />
        </el:formGroup>

        <el:formGroup>
            <el:textArea name="note" size="8"  class=""
                         label="${message(code:'endorseOrder.note.label',default:'note')}"
                         value="${endorseOrder?.note}"/>
        </el:formGroup>


    </lay:widgetBody>
</lay:widget>
