/*
 * Copyright (c) 2015 by Johannes Dillmann, Zuse Institute Berlin
 *
 * Licensed under the BSD License, see LICENSE file for details.
 *
 */
package org.xtreemfs.common.libxtreemfs;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import org.xtreemfs.common.libxtreemfs.swig.ClientProxy;
import org.xtreemfs.common.libxtreemfs.swig.OptionsProxy;
import org.xtreemfs.common.libxtreemfs.swig.SSLOptionsProxy;
import org.xtreemfs.common.libxtreemfs.swig.ServiceAddresses;
import org.xtreemfs.common.libxtreemfs.swig.StringMap;
import org.xtreemfs.common.libxtreemfs.swig.StringVector;
import org.xtreemfs.foundation.SSLOptions;
import org.xtreemfs.foundation.pbrpc.generatedinterfaces.RPC.UserCredentials;
import org.xtreemfs.pbrpc.generatedinterfaces.GlobalTypes.KeyValuePair;

public final class NativeHelper {

    public static ClientProxy createClientProxy(String[] dirServiceAddressesArray, UserCredentials userCredentials,
            SSLOptions sslOptions, Options options) {

        // TODO (jdillmann): Think about moving this to the factory.
        StringVector dirServiceAddressesVector = StringVector.from(Arrays.asList(dirServiceAddressesArray));
        ServiceAddresses dirServiceAddresses = new ServiceAddresses(dirServiceAddressesVector);
        OptionsProxy optionsProxy = NativeHelper.migrateOptions(options);
        SSLOptionsProxy sslOptionsProxy = null;
        if (sslOptions != null) {
            // TODO (jdillmann): Merge from sslOptions
            throw new RuntimeException("SSLOptions are not supported yet.");
        }

        return ClientProxy.createClient(dirServiceAddresses, userCredentials, sslOptionsProxy, optionsProxy);
    }

    public static OptionsProxy migrateOptions(Options o) {
        OptionsProxy op = new OptionsProxy();

        // Migrate the options, that are setable as Java Options
        op.setMetadata_cache_size(BigInteger.valueOf(o.getMetadataCacheSize()));
        op.setMax_tries(o.getMaxTries());
        op.setMax_read_tries(o.getMaxReadTries());
        op.setMax_view_renewals(o.getMaxViewRenewals());
        // TODO (jdillmann): check if maxWriteahed is in bytes or kb
        op.setAsync_writes_max_request_size_kb(o.getMaxWriteahead());
        op.setEnable_async_writes(o.getMaxWriteahead() > 0);
        op.setPeriodic_file_size_updates_interval_s(o.getPeriodicFileSizeUpdatesIntervalS());
        op.setReaddir_chunk_size(o.getReaddirChunkSize());

        // Migrate also Java defaults (TODO: ?)
        op.setConnect_timeout_s(o.getConnectTimeout_s());
        // o.getInterruptSignal()
        op.setLinger_timeout_s(o.getLingerTimeout_s());
        op.setAsync_writes_max_requests(o.getMaxWriteaheadRequests());
        op.setMetadata_cache_size(BigInteger.valueOf(o.getMetadataCacheSize()));
        op.setMetadata_cache_ttl_s(BigInteger.valueOf(o.getMetadataCacheTTLs()));
        op.setPeriodic_xcap_renewal_interval_s(o.getPeriodicXcapRenewalIntervalS());
        op.setRequest_timeout_s(o.getRequestTimeout_s());
        op.setRetry_delay_s(o.getRetryDelay_s());

        return op;
    }

    public static StringMap keyValueListToMap(List<KeyValuePair> list) {
        StringMap map = new StringMap();
        for (KeyValuePair kv : list) {
            map.set(kv.getKey(), kv.getValue());
        }
        return map;
    }
}
