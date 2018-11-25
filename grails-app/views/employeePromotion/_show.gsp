<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${employeePromotion?.employee?.militaryNumber}" type="String" label="${message(code:'employee.militaryNumber.label',default:'militaryNumber')}" />
    <lay:showElement value="${employeePromotion?.militaryRank}" type="MilitaryRank" label="${message(code:'employeePromotion.militaryRank.label',default:'militaryRank')}" />
    <lay:showElement value="${employeePromotion?.militaryRankClassification}" type="MilitaryRankClassification" label="${message(code:'employeePromotion.militaryRankClassification.label',default:'militaryRankType')}" />
    <lay:showElement value="${employeePromotion?.militaryRankType}" type="MilitaryRankType" label="${message(code:'employeePromotion.militaryRankType.label',default:'militaryRankType')}" />
    <lay:showElement value="${employeePromotion?.militaryRankTypeDate}" type="ZonedDate" label="${message(code:'employeePromotion.militaryRankTypeDate.label',default:'managerialRankDate')}" />
    <lay:showElement value="${employeePromotion?.actualDueDate}" type="ZonedDate" label="${message(code:'employeePromotion.actualDueDate.label',default:'actualDueDate')}" />
    <lay:showElement value="${employeePromotion?.dueDate}" type="ZonedDate" label="${message(code:'employeePromotion.dueDate.label',default:'dueDate')}" />
    <lay:showElement value="${employeePromotion?.dueReason}" type="enum" label="${message(code:'employeePromotion.dueReason.label',default:'dueReason')}" messagePrefix="EnumPromotionReason" />
    <lay:showElement value="${employeePromotion?.managerialOrderNumber}" type="String" label="${message(code:'employeePromotion.managerialOrderNumber.label',default:'managerialOrderNumber')}" />
    <lay:showElement value="${employeePromotion?.orderDate}" type="ZonedDate" label="${message(code:'employeePromotion.orderDate.label',default:'managerialOrderNumber')}" />
    <lay:showElement value="${employeePromotion?.requestSource?.id}" type="Long" label="${message(code:'employeePromotion.requestSource.label',default:'requestSource')}" />
    <lay:showElement value="${employeePromotion?.note}" type="String" label="${message(code:'employeePromotion.note.label',default:'note')}" />

</lay:showWidget>