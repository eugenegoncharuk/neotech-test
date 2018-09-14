package com.neotech.task.service;
/*
 * Copyright C.T.Co Ltd, 15/25 Jurkalnes Street, Riga LV-1046, Latvia. All rights reserved.
 */

import java.util.Optional;

public class TreeNode {

    public TreeNode[] nextNodes = new TreeNode[10];

    public Optional<String> country = Optional.empty();

    public String countryCode;
}
