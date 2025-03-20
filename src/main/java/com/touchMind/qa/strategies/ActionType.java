package com.touchMind.qa.strategies;

public interface ActionType extends ImplicitActionType {
    String CASCADE_TEST_PLAN_ACTION = "CascadeTestPlanAction";
    String CHECK_IF_ELEMENT_PRESENT = "CheckIfElementsPresent";
    String CLICK_ACTION = "Click";
    String INPUT_TEXT = "EnterText";
    String FORCE_CLICK = "ForceClick";
    String FORCE_WAIT_ACTION = "ForceWaitAction";
    String MOUSE_OVER_ACTION = "MouseOverAction";
    String OPEN_URL_ACTION = "OpenUrlAction";
    String REMOVE_ELEMENT_FROM_PAGE = "RemoveElement";
    String SELECT_DROP_DOWN = "SelectDropdown";
    String SWITCH_TO_IFRAME = "SwitchToIframe";
    String SWITCH_TO_MAIN_PAGE = "SwitchToMainPage";
    String WAIT_FOR_POPUP_AND_SWITCH = "WaitForPopupAndSwitch";
    String DOM_PARSE_ACTION = "DomParseAction";
    String CALCULATE_ACTION = "Calculate";
    String CRAWLER_ACTION = "TreeAction";
    String GROUP_ACTION = "GroupAction";
}
