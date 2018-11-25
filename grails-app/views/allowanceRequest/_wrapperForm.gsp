<lay:widget transparent="true" color="blue" icon="icon-user"
            title="${g.message(code: "allowanceRequest.label")}">

    <lay:widgetBody>
        <lay:showWidget size="6">

            <lay:showElement value="${allowanceRequest?.allowanceType?.descriptionInfo?.localName}" type="VacationType"
                             label="${message(code: 'allowanceRequest.allowanceType.label', default: 'allowanceType')}"/>

            <lay:showElement value="${allowanceRequest?.effectiveDate}" type="ZonedDate"
                             label="${message(code: 'allowanceRequest.effectiveDate.label', default: 'effectiveDate')}"/>

        </lay:showWidget>

        <lay:showWidget size="6">
            <lay:showElement value="${allowanceRequest?.toDate}" type="ZonedDate"
                             label="${message(code: 'allowanceRequest.toDate.label', default: 'toDate')}"/>
        </lay:showWidget>




        <el:row/>
    </lay:widgetBody>
</lay:widget>
