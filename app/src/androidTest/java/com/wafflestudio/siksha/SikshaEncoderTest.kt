package com.wafflestudio.siksha

import androidx.test.runner.AndroidJUnit4
import com.squareup.moshi.Moshi
import com.wafflestudio.siksha.util.SikshaEncoder
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SikshaEncoderTest {
    private val moshi = Moshi.Builder().build()
    private val sikshaEncoder = SikshaEncoder("secret", moshi)

    @Test
    fun `encode`() {
        val expected = "eyJKV1QiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJkZXZpY2UiOiJkZXZpY2UiLCJkZXZpY2VfdHlwZSI6ImEiLCJtZWFsX2lkIjoxLCJzY29yZSI6NC41fQ.x9vh8oGnU8UCnpaLh_kxoVTeVfX2WOKjVhyHDUAOtAM"
        val encoded = sikshaEncoder.encode(1, 4.5f, "device")
        Assert.assertEquals(expected, encoded)
    }
}