package com.neotech.task.utils;
/*
 * Copyright C.T.Co Ltd, 15/25 Jurkalnes Street, Riga LV-1046, Latvia. All rights reserved.
 */

import java.util.Optional;

public class GraphNode {

    GraphNode[] nextNodes = new GraphNode[10];

    Optional<String> value = Optional.empty();
}
