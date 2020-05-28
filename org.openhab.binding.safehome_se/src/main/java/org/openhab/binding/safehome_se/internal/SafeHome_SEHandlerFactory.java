/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.safehome_se.internal;

import static org.openhab.binding.safehome_se.internal.SafeHome_SEBindingConstants.*;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandlerFactory;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandlerFactory;
import org.osgi.service.component.annotations.Component;

/**
 * The {@link SafeHome_SEHandlerFactory} is responsible for creating things and thing
 * handlers.
 *
 * @author Alessandro Verga - Initial contribution
 */
@NonNullByDefault
@Component(configurationPid = "binding.safehome_se", service = ThingHandlerFactory.class)
public class SafeHome_SEHandlerFactory extends BaseThingHandlerFactory {

    // default used this
    //private static final Set<ThingTypeUID> SUPPORTED_THING_TYPES_UIDS = Collections.singleton(SafeHome_SEBindingConstants.THING_TYPE_SAMPLE);

    @Override
    public boolean supportsThingType(ThingTypeUID thingTypeUID) {
        return SUPPORTED_THING_TYPES.contains(thingTypeUID);
    }

    @Override
    protected @Nullable ThingHandler createHandler(Thing thing) {
        // "supports thing type" has already returned true when we're here
        return new SafeHome_SEHandler(thing);
    }
}
